package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.core.io.support.ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.persistence.SchemaResourceFileRepository;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 21.03.22
 */

@Component
@Slf4j
public class SchemaResourceFileService implements SchemaResourceFileBoundary {

  private static final String HSP_SCHEMA_RESOURCES = CLASSPATH_ALL_URL_PREFIX + "hsp-schemaresources/**/*.*";

  private final String schemaResourcesDirectory;
  private final SchemaResourceFileRepository schemaResourceFileRepository;
  private final ResourceLoader resourceLoader;

  @Autowired
  public SchemaResourceFileService(
      @Value("${import.schemaresourcesdirectory}") String schemaResourcesDirectory,
      SchemaResourceFileRepository schemaResourceFileRepository,
      ResourceLoader resourceLoader) {

    this.schemaResourcesDirectory = schemaResourcesDirectory;
    this.schemaResourceFileRepository = schemaResourceFileRepository;
    this.resourceLoader = resourceLoader;
  }

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("SchemaResourceFileService: onApplicationEvent started... {}", event);

    Map<String, SchemaResourceFile> schemaResourceFiles = readClasspathResources();

    for (String resourceId : SCHEMA_RESOURCE_IDS) {
      log.info("Check resourceId {}", resourceId);
      Optional<SchemaResourceFile> resourceFileOptional;
      try {
        resourceFileOptional = findOptionalById(resourceId);
      } catch (Exception e) {
        throw new SchemaResourceFileRuntimeException("Error reading resource-file for id " + resourceId, e);
      }

      SchemaResourceFile schemaResourceFile = schemaResourceFiles.get(resourceId);
      if (Objects.isNull(schemaResourceFile) || schemaResourceFile.getDatei().isEmpty()) {
        throw new SchemaResourceFileRuntimeException("Missing resource-file in classpath for id " + resourceId);
      }

      resourceFileOptional.ifPresentOrElse(
          existingResourceFile -> {
            if (existingResourceFile.getVersion().compareTo(schemaResourceFile.getVersion()) < 0) {
              log.info("replacing resource-file {} version {} by version {}.",
                  existingResourceFile.getDateiName(),
                  existingResourceFile.getVersion(),
                  schemaResourceFile.getVersion());
              copyAndSaveResourceFile(schemaResourceFile);
              delete(existingResourceFile);
            }
          },
          () -> copyAndSaveResourceFile(schemaResourceFile));
    }
    log.info("SchemaResourceFileService: onApplicationEvent finished.");
  }

  @Override
  public Iterable<SchemaResourceFile> findAll() {
    return schemaResourceFileRepository.findAll();
  }

  @Override
  public Optional<SchemaResourceFile> findOptionalById(String id) throws IOException {
    if (Objects.isNull(id) || id.isEmpty()) {
      return Optional.empty();
    }

    Optional<SchemaResourceFile> optionalSchemaResourceFile = schemaResourceFileRepository.findById(id);
    if (optionalSchemaResourceFile.isPresent()) {
      SchemaResourceFile schemaResourceFile = optionalSchemaResourceFile.get();
      String resourcePath = createResourcePath(schemaResourceFile);
      Resource resource = resourceLoader.getResource(resourcePath);

      if (!resource.exists() || !resource.isReadable()) {
        throw new IOException("For SchemaResourceFile " + id + " no file exists: " + resourcePath);
      } else {
        log.info("For SchemaResourceFile {} using resource {}", id, resourcePath);
        optionalSchemaResourceFile.get().setDatei(Optional.of(resource));
      }
    }

    return optionalSchemaResourceFile;
  }

  @Override
  public void save(String id, byte[] datei, String bearbeitername, Version version)
      throws SchemaResourceFileException {
    log.info("save SchemaResourceFile: id={}", id);
    Objects.requireNonNull(id, "Id is required!");
    Objects.requireNonNull(datei, "datei is required!");
    Objects.requireNonNull(bearbeitername, "bearbeitername is required!");
    Objects.requireNonNull(version, "Version is required!");

    if (datei.length == 0) {
      throw new SchemaResourceFileException("Empty datei");
    }

    SchemaResourceFile schemaResourceFile = schemaResourceFileRepository.findById(id)
        .orElseThrow(() -> new SchemaResourceFileException("Found no SchemaResourceFile for id: " + id));

    schemaResourceFile.setVersion(version);
    schemaResourceFile.setBearbeitername(bearbeitername);
    schemaResourceFile.setAenderungsDatum(LocalDateTime.now());

    String resourcePath = createResourcePath(schemaResourceFile);
    Resource resource = resourceLoader.getResource(resourcePath);

    if (!resource.isFile()) {
      throw new SchemaResourceFileException("Can't write file to path " + resourcePath);
    }

    try {
      Path path = resource.getFile().toPath();
      Files.createDirectories(path.getParent());
      Files.write(path, datei);
    } catch (IOException e) {
      throw new SchemaResourceFileException("Error writing file for id " + id, e);
    }

    schemaResourceFileRepository.save(schemaResourceFile);
  }

  Map<String, SchemaResourceFile> readClasspathResources() {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    try {
      Resource[] resources = resolver.getResources(HSP_SCHEMA_RESOURCES);
      return Arrays.stream(resources)
          .filter(Resource::isReadable)
          .map(this::createSchemaResourceFile)
          .collect(Collectors.toMap(SchemaResourceFile::getId, Function.identity()));
    } catch (Exception e) {
      throw new SchemaResourceFileRuntimeException("Error reading files from schemaResourcesFolder!", e);
    }
  }

  SchemaResourceFile createSchemaResourceFile(Resource fileResource) {
    log.info("createSchemaResourceFile: fileResource={}", fileResource);

    String[] splitt;
    URL fileURL;
    try {
      fileURL = fileResource.getURL();
      splitt = fileURL.toString().split("/");
    } catch (Exception e) {
      throw new SchemaResourceFileRuntimeException("Error parsing fileResource " + fileResource, e);
    }

    if (splitt.length < 5) {
      throw new SchemaResourceFileRuntimeException("Invalid fileResource " + fileResource);
    }

    return SchemaResourceFile.builder()
        .withDateiName(splitt[splitt.length - 1])
        .withId(UUID.nameUUIDFromBytes(splitt[splitt.length - 1].getBytes(StandardCharsets.UTF_8)).toString())
        .withXmlFormat(DateiFormate.valueOf(splitt[splitt.length - 2].toUpperCase(Locale.ROOT)))
        .withSchemaResourceTyp(SchemaResourceTyp.valueOf(splitt[splitt.length - 3].toUpperCase(Locale.ROOT)))
        .withVersion(Version.parse(splitt[splitt.length - 4]))
        .withErstellungsDatum(LocalDateTime.now())
        .withAenderungsDatum(LocalDateTime.now())
        .withBearbeitername("system")
        .withDatei(Optional.of(fileResource))
        .build();
  }

  String createResourcePath(SchemaResourceFile schemaResourceFile) {
    return schemaResourcesDirectory
        + '/' + schemaResourceFile.getVersion()
        + '/' + schemaResourceFile.getSchemaResourceTyp().toString().toLowerCase()
        + '/' + schemaResourceFile.getXmlFormat().toString().toLowerCase()
        + '/' + schemaResourceFile.getDateiName();
  }

  void copyAndSaveResourceFile(SchemaResourceFile schemaResourceFile) {
    try {
      String resourcePath = createResourcePath(schemaResourceFile);
      File resourceFile = ResourceUtils.getFile(resourcePath);
      Path path = resourceFile.toPath();
      Files.createDirectories(path.getParent());
      if (schemaResourceFile.getDatei().isPresent()) {
        Files.copy(schemaResourceFile.getDatei().get().getInputStream(), path, REPLACE_EXISTING);
      }

      schemaResourceFileRepository.save(schemaResourceFile);
    } catch (Exception e) {
      throw new SchemaResourceFileRuntimeException("Error copying resource from classpath for id "
          + schemaResourceFile.getId(), e);
    }
  }

  void delete(SchemaResourceFile schemaResourceFile) {
    String resourcePath = createResourcePath(schemaResourceFile);
    try {
      if (schemaResourceFile.getDatei().isPresent()) {

        File resourceFile = ResourceUtils.getFile(resourcePath);
        Path path = resourceFile.toPath();
        Files.deleteIfExists(path);
      }
    } catch (Exception e) {
      log.error("Error deleting resource " + resourcePath, e);
    }
  }

}

package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 27.03.2019.
 */
@Component
@Slf4j
public class FileService implements DateiBoundary {

  @Value("${import.maxUnpackedFileSize}")
  private long maxUnpackedFileSize;

  @Autowired
  private MessageSource messageSource;

  public boolean save(ImportFile importFile, byte[] content) {
    boolean couldBeSaved = true;

    try {
      Files.write(importFile.getPath(), content);

      log.info("Import[saveAndRemoveBOMFromXML]: saved: " + importFile.getPath());
    } catch (IOException e) {
      log.error("Error during save file!", e);
      importFile.applyError(
          messageSource.getMessage("import.error.save", null, LocaleContextHolder.getLocale()));

      couldBeSaved = false;
    }

    return couldBeSaved;
  }

  List<ImportFile> unzip(final ImportFile importFile, final String importDir) {
    final List<ImportFile> importFilesToAdd = new ArrayList<>();

    try (ZipInputStream zipInputStream = new ZipInputStream(
        Files.newInputStream(importFile.getPath()),
        StandardCharsets.UTF_8)) {
      ZipEntry entry;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        importFilesToAdd.addAll(unzipEntry(importDir, entry, zipInputStream));
      }
    } catch (Exception e) {
      importFile.applyError(
          messageSource.getMessage("import.error.unzip", null, LocaleContextHolder.getLocale()));
    }

    return importFilesToAdd;
  }

  List<ImportFile> unzipEntry(final String importDir, final ZipEntry entry,
      final ZipInputStream zipInputStream)
      throws IOException {
    List<ImportFile> result = new ArrayList<>();

    if (entry.getSize() > maxUnpackedFileSize) {
      throw new IOException(
          "Unpacked file size to big! (file size: " + entry.getSize() + ", max size: "
              + maxUnpackedFileSize + ")");
    }
    Objects.requireNonNull(entry, "Unexpected nullEntry in the zip");
    Objects.requireNonNull(importDir, "ImportDir is null");
    String orgName = entry.getName();
    final Path toPath = Paths.get(importDir).resolve(orgName);

    if (entry.isDirectory() || !orgName.endsWith(".xml")) {
      throw new IOException("Zip has entries which are not allowed: " + orgName);
    }
    Files.copy(zipInputStream, toPath, StandardCopyOption.REPLACE_EXISTING);

    log.info("Import[unzipAndRemoveBOM]: file extracted: {}", toPath);

    final ImportFile newResult = ImportFile.builder().id(UUID.randomUUID().toString())
        .dateiName(orgName)
        .path(toPath)
        .dateiFormat(DateiFormate.UNBEKANNT).error(false)
        .message(null)
        .build();
    result.add(newResult);

    return result;
  }

  public void removeBOM(ImportFile importFile) {
    Objects.requireNonNull(importFile, "ImportFile is null");
    Objects.requireNonNull(importFile.getPath(), "ImportFile has no path");
    final String sourceFileName = importFile.getPath().toFile().getAbsolutePath();
    String orgFileName = importFile.getPath().getFileName().toString();
    final String targetFileName =
        sourceFileName.substring(0, sourceFileName.length() - 4) + "-wthout-BOM.xml";
    final File targetFile = new File(targetFileName);

    final File sourceFile = importFile.getPath().toFile();
    try (InputStream is = new BOMInputStream(new FileInputStream(sourceFile))) {
      Files.copy(is, targetFile.toPath());
    } catch (Exception e) {
      importFile.applyError(messageSource.getMessage("import.error.remove.bom", null,
          LocaleContextHolder.getLocale()));
    }

    importFile.setPath(targetFile.toPath());
    importFile.setDateiName(orgFileName);
  }

  public String identifyFileType(Path file) {

    AtomicReference<String> type = new AtomicReference<>();

    try {
      Optional.ofNullable(Files.probeContentType(file))
          .ifPresentOrElse(type::set, () -> type.set("unkown"));
    } catch (IOException e) {
      log.error("Error identify the file type ", e);
    }

    return type.get();
  }

  public boolean isSupportedContent(MultipartFile datei) throws IOException {
    boolean result = false;

    if (Objects.nonNull(datei) && Objects.nonNull(datei.getName()) && Objects.nonNull(
        datei.getBytes())
        && isAllowedContentType(datei.getContentType())) {
      result = true;
    }

    return result;
  }

  public void saveStringParts(final String filename, final String[] parts) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(filename, StandardCharsets.UTF_8))) {
      for (String part : parts) {
        writer.write(part);
      }
    }
  }

  boolean isAllowedContentType(String dateiTyp) {
    if (Objects.isNull(dateiTyp)) {
      return false;
    }

    log.info("Check file for content type {} ", dateiTyp);

    return dateiTyp.contains("zip") || dateiTyp.contains("xml");
  }

}

package staatsbibliothek.berlin.hsp.importservice.domain.service;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 05.06.2020.
 * @version 1.0
 */

@Slf4j
public abstract class ImportServiceTemplate implements DateiImportBoundary {

  static final String DATE_TIME_FORMAT = "yyyy.MM.dd.HH.mm.ss.SSS";
  Set<String> knownType = new HashSet<>(
      Arrays.asList("application/x-zip-compressed", "text/xml", "application/zip",
          "application/xml", "text/turtle"));
  String dataDirectory;
  XMLService xmlService;

  RDFService rdfService;
  FileService fileService;
  ImportJobRepository importJobRepository;
  ImportJobConvert importJobConvert;

  public static void cleanUpImportDirectory(ImportJob importJob) {
    if (importJob != null && importJob.getImportFiles() != null) {
      for (ImportFile importFile : importJob.getImportFiles()) {
        Path path = importFile.getPath();
        if (path != null && Files.exists(path)) {
          try (Stream<Path> walk = Files.walk(path.getParent())) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
          } catch (Exception e) {
            log.warn("Unable to delete {}", path.getParent());
          }
        }
      }
    }
  }

  public void identifyXMLFormatAndValidate(ImportJob importJob) {

    executeStepWithImportFile(importJob,
        file -> !file.isError() && isXML(file),
        file -> xmlService.applyXMLFormat(file));

    executeStepWithImportFile(importJob,
        file -> !file.isError() && isXML(file),
        file -> xmlService.validateSchema(file));

    importJobRepository.save(importJob);
  }

  public void identifyRDFFormatAndValidate(ImportJob importJob) {
    executeStepWithImportFile(importJob,
        file -> !file.isError() && isRDF(file),
        file -> rdfService.applyRDFFormat(file));

    executeStepWithImportFile(importJob,
        file -> !file.isError() && isRDF(file),
        file -> rdfService.validateSchema(file, importJob));

    importJobRepository.save(importJob);
  }

  public void unzip(ImportJob importJob) {
    // Unzip
    final List<ImportFile> importFilesToRemove = new ArrayList<>();
    final List<ImportFile> importFilesToAdd = new ArrayList<>();

    executeStepWithImportFile(importJob, file -> !file.isError(),
        file -> handleFileByType(importJob, importFilesToRemove, importFilesToAdd, file,
            fileService));
    importJobRepository.save(importJob);

    log.info("File Type analysed");

    importJob.getImportFiles().addAll(importFilesToAdd);

    importJob.getImportFiles()
        .removeIf(file -> !file.isError() && importFilesToRemove.contains(file));
  }

  public static ImportFile createImportFile(String fileName, ImportJob importJob) {
    final Path initialPath = Paths.get(importJob.getImportDir(), importJob.getName());
    final ImportFile initialImportFile = ImportFile.builder().id(UUID.randomUUID().toString())
        .dateiName(fileName)
        .path(initialPath)
        .dateiFormat(DateiFormate.UNBEKANNT).error(false)
        .message("")
        .build();
    importJob.getImportFiles().add(initialImportFile);
    return initialImportFile;
  }

  public ImportJob initialise(String fileName, String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) {

    final File importDir = Paths.get(dataDirectory, "hsp-import-", LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))).toFile();

    importDir.mkdirs();

    ImportJob result = new ImportJob(importDir.getAbsolutePath(), fileName, benutzerName,
        activityStreamsDokumentTyp.name(), TotalResult.NO_RESULT, "");

    importJobRepository.save(result);
    return result;
  }

  public void executeStepWithImportFile(ImportJob job, Predicate<ImportFile> file,
      Consumer<ImportFile> action) {

    for (ImportFile importFile : job.getImportFiles()) {
      if (file.test(importFile)) {
        action.accept(importFile);
      }
    }
  }

  private void handleFileByType(ImportJob result, List<ImportFile> importFilesToRemove,
      List<ImportFile> importFilesToAdd, ImportFile importFile, FileService fileService) {

    importFile.setDateiTyp(fileService.identifyFileType(importFile.getPath()));

    log.info("Handle File and find Type {} ", importFile.getDateiTyp());

    if (isZip(importFile)) {
      importFilesToAdd.addAll(fileService.unzip(importFile, result.getImportDir()));
      importFilesToRemove.add(importFile);
    }

    if (!knownType.contains(importFile.getDateiTyp())) {
      importFile.setError(true);
      importFile.setMessage("Unknown File Type " + importFile.getDateiTyp());
    }
  }

  private static boolean isZip(ImportFile importFile) {
    return importFile.getDateiTyp().contains("zip");
  }

  private static boolean isXML(ImportFile importFile) {
    return importFile.getDateiTyp().contains("xml");
  }

  private static boolean isRDF(ImportFile importFile) {
    return importFile.getDateiTyp().contains("turtle");
  }

  public ActivityStream mapFileToMessage(ImportFile file, ImportJob job,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp)
      throws IOException, ActivityStreamsException {

    final byte[] xmlContent = Files.readAllBytes(file.getPath());

    ActivityStreamObject uploadObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(activityStreamsDokumentTyp)
        .withContent(xmlContent)
        .withName(file.getDateiName())
        .withMediaType(file.getDateiTyp())
        .build();

    ActivityStreamObject importJobObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.IMPORT)
        .withContent(importJobConvert.toJson(job))
        .withName(job.getName())
        .withMediaType("application/json")
        .build();

    return ActivityStream
        .builder()
        .withId(job.getId())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName(job.getBenutzerName())
        .withTargetName(job.getName())
        .addObject(uploadObject)
        .addObject(importJobObject)
        .build();

  }

}

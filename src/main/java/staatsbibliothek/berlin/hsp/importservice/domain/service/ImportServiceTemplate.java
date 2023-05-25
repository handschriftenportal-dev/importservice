/*
 * MIT License
 *
 * Copyright (c) 2023 Staatsbibliothek zu Berlin - Preußischer Kulturbesitz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 05.06.2020.
 * @version 1.0
 */

@Slf4j
public abstract class ImportServiceTemplate implements DateiImportBoundary {

  final String datetimetemplate = "yyyy.MM.dd.HH.mm.ss.SSS";
  Set<String> knownType = new HashSet<>(
      Arrays.asList("application/x-zip-compressed", "text/xml", "application/zip", "application/xml"));
  String dataDirectory;
  XMLService xmlService;
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
    executeStepWithImportFile(importJob, file -> !file.isError(), file -> xmlService.applyXMLFormat(file));

    executeStepWithImportFile(importJob, file -> !file.isError(), file -> xmlService.validateSchema(file));

    importJobRepository.save(importJob);
  }

  public void unzip(ImportJob importJob) {
    // Unzip
    final List<ImportFile> importFilesToRemove = new ArrayList<>();
    final List<ImportFile> importFilesToAdd = new ArrayList<>();

    executeStepWithImportFile(importJob, file -> !file.isError(),
        file -> handleFileByType(importJob, importFilesToRemove, importFilesToAdd, file, fileService));
    importJobRepository.save(importJob);

    log.info("File Type analysed");

    importJob.getImportFiles().addAll(importFilesToAdd);

    importJob.getImportFiles().removeIf(file -> !file.isError() && importFilesToRemove.contains(file));
  }

  public ImportFile createImportFile(String fileName, ImportJob importJob) {
    final Path initialPath = Paths.get(importJob.getImportDir(), importJob.getName());
    final ImportFile initialImportFile = new ImportFile(UUID.randomUUID().toString(), fileName, initialPath, null,
        XMLFormate.UNBEKANNT, false, null);
    importJob.getImportFiles().add(initialImportFile);
    return initialImportFile;
  }

  public ImportJob initialise(String fileName, String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) {

    final File importDir = Paths.get(dataDirectory, "hsp-import-", LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(datetimetemplate))).toFile();

    importDir.mkdirs();

    ImportJob result = new ImportJob(importDir.getAbsolutePath(), fileName, benutzerName,
        activityStreamsDokumentTyp.name(), TotalResult.NO_RESULT, "");

    importJobRepository.save(result);
    return result;
  }

  public void executeStepWithImportFile(ImportJob job, Predicate<ImportFile> file, Consumer<ImportFile> action) {

    for (ImportFile importFile : job.getImportFiles()) {
      if (file.test(importFile)) {
        action.accept(importFile);
      } else {
        log.error("executeStepWithImportFile -> FAILED errormessage= " + importFile.getMessage() + " Dateiname= " + importFile.getDateiName());
        job.setResult(TotalResult.FAILED);
        job.setErrorMessage(importFile.getMessage());
      }
    }
  }

  public void handleFileByType(ImportJob result, List<ImportFile> importFilesToRemove,
      List<ImportFile> importFilesToAdd, ImportFile importFile, FileService fileService) {
    if (fileService.identifyFileType(importFile.getPath()).contains("zip")) {
      importFilesToAdd.addAll(fileService.unzip(importFile, result.getImportDir()));

      importFilesToRemove.add(importFile);
    } else if (knownType.contains(fileService.identifyFileType(importFile.getPath()))) {
      importFile.setDateiTyp(fileService.identifyFileType(importFile.getPath()));
    } else {
      importFile.setError(true);
      importFile
          .setMessage("Unkown File Type " + fileService.identifyFileType(importFile.getPath()));
    }
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

    ActivityStream message = ActivityStream
        .builder()
        .withId(job.getId())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName(job.getBenutzerName())
        .withTargetName(job.getName())
        .addObject(uploadObject)
        .addObject(importJobObject)
        .build();

    return message;

  }

}

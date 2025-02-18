package staatsbibliothek.berlin.hsp.importservice.domain.service;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNachweisProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 07.07.21
 */

@Component
@Slf4j
@Qualifier("KulturObjektDokumenteImport")
public class KulturObjektDokumentImportService extends ImportServiceTemplate {

  KafkaNachweisProducer kafkaNachweisProducer;

  public KulturObjektDokumentImportService(@Value("${import.datadirectory}") String dataDirectory,
      XMLService xmlService, FileService fileService,
      KafkaNachweisProducer kafkaNachweisProducer,
      ImportJobRepository importJobRepository, ImportJobConvert importJobConvert) {
    this.dataDirectory = dataDirectory;
    this.xmlService = xmlService;
    this.fileService = fileService;
    this.kafkaNachweisProducer = kafkaNachweisProducer;
    this.importJobRepository = importJobRepository;
    this.importJobConvert = importJobConvert;
  }

  @Override
  public ImportJob importDateien(ActivityStreamObject activityStreamObject, String fileName, String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) throws ActivityStreamsException {

    ImportJob importJob = initialise(fileName, benutzerName, activityStreamsDokumentTyp);
    final ImportFile initialImportFile = createImportFile(fileName, importJob);

    if (fileService.save(initialImportFile, activityStreamObject.getContent())) {

      unzip(importJob);

      // Remove BOM
      executeStepWithImportFile(importJob, file -> !file.isError(), file -> fileService.removeBOM(file));
      importJobRepository.save(importJob);
      log.info("BOM removed.");

      identifyXMLFormatAndValidate(importJob);
      log.info("XML identified and validated.");

      importJob.getImportFiles().stream().filter(ImportFile::isError).findFirst().ifPresentOrElse((f) -> {
        importJob.setResult(TotalResult.FAILED);
        importJob.setErrorMessage("One ore more files are failed with " + f.getMessage());
      }, () -> executeStepWithImportFile(importJob, file -> !file.isError(),
          file -> sendKafkaMessage(file, importJob, activityStreamsDokumentTyp)));
    }

    importJobRepository.save(importJob);

    cleanUpImportDirectory(importJob);

    return importJob;
  }

  private void sendKafkaMessage(ImportFile file, ImportJob importJob,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) {

    try {
      kafkaNachweisProducer
          .sendMessageWithActivityStream(mapFileToMessage(file, importJob, activityStreamsDokumentTyp));
    } catch (IOException | ActivityStreamsException e) {
      log.error("Error during sending kafka message to import.topic!", e);
      importJob.setErrorMessage(e.getMessage());
      importJob.setResult(TotalResult.FAILED);
    }
  }

}

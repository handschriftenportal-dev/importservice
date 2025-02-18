package staatsbibliothek.berlin.hsp.importservice.domain.service;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNormdatenProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 05.06.2020.
 * @version 1.0
 */

@Component
@Slf4j
@Qualifier("NormdatenImport")
public class NormdatenImportService extends ImportServiceTemplate {

  @Autowired
  public NormdatenImportService(@Value("${import.datadirectory}") String dataDirectory,
      XMLService xmlService, FileService fileService,
      KafkaNormdatenProducer kafkaNormdatenProducer,
      ImportJobRepository importJobRepository, ImportJobConvert importJobConvert,
      RDFService rdfService) {
    this.dataDirectory = dataDirectory;
    this.xmlService = xmlService;
    this.fileService = fileService;
    this.kafkaNormdatenProducer = kafkaNormdatenProducer;
    this.importJobRepository = importJobRepository;
    this.importJobConvert = importJobConvert;
    this.rdfService = rdfService;
  }

  KafkaNormdatenProducer kafkaNormdatenProducer;

  public ImportJob importDateien(ActivityStreamObject activityStreamObject, String fileName,
      String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp)
      throws ActivityStreamsException {

    ImportJob importJob = initialise(fileName, benutzerName, activityStreamsDokumentTyp);
    final ImportFile initialImportFile = createImportFile(fileName, importJob);

    if (fileService.save(initialImportFile, activityStreamObject.getContent())) {

      unzip(importJob);

      identifyXMLFormatAndValidate(importJob);

      identifyRDFFormatAndValidate(importJob);

      importJob.getImportFiles().stream().filter(ImportFile::isError).findFirst()
          .ifPresentOrElse((f) -> {

            importJob.setResult(TotalResult.FAILED);
            importJob.setErrorMessage("One ore more files are failed with " + f.getMessage());
          }, () -> executeStepWithImportFile(importJob, file -> !file.isError(),
              file -> sendKafkaMessage(file, importJob, activityStreamsDokumentTyp)));

    }

    importJobRepository.save(importJob);
    cleanUpImportDirectory(importJob);

    return importJob;
  }

  void sendKafkaMessage(ImportFile file, ImportJob job,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) {
    try {
      kafkaNormdatenProducer.sendMessage(mapFileToMessage(file, job, activityStreamsDokumentTyp));
    } catch (IOException | ActivityStreamsException e) {
      log.error("Error during sending kafka message to normdaten topic!", e);
      job.setErrorMessage(e.getMessage());
      job.setResult(TotalResult.FAILED);
    }
  }

  public String getDataDirectory() {
    return dataDirectory;
  }

  public XMLService getXmlService() {
    return xmlService;
  }

  public FileService getFileService() {
    return fileService;
  }
}

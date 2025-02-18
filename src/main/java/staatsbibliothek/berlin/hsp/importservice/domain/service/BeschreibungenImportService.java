package staatsbibliothek.berlin.hsp.importservice.domain.service;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamObjectTagId;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNachweisProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@Component
@Slf4j
@Qualifier("BeschreibungsImport")
public class BeschreibungenImportService extends ImportServiceTemplate {

  public static final String HSP_FORMAT_VALIDATION_AS_STRING = ActivityStreamObjectTagId.DEACTIVATE_HSP_FORMAT_VALIDATION
      .toString();
  public static final String INTERN_EXTERN_AS_STRING = ActivityStreamObjectTagId.INTERN_EXTERN.toString();
  @Autowired
  KafkaNachweisProducer kafkaNachweisProducer;
  @Autowired
  private MessageSource messageSource;

  public ImportJob importDateien(ActivityStreamObject activityStreamObject, final String fileName, String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp)
      throws ActivityStreamsException {

    ImportJob importJob = initialise(fileName, benutzerName, activityStreamsDokumentTyp);

    importJobRepository.save(importJob);

    Optional<ActivityStreamObjectTag> internExternTag = activityStreamObject.getTag()
        .stream()
        .filter(t -> INTERN_EXTERN_AS_STRING.equals(t.getId()))
        .findFirst();

    log.info("Start processing Beschreibungen Job {} ", importJob.getId());

    // Save
    final ImportFile initialImportFile = createImportFile(fileName, importJob);

    if (fileService.save(initialImportFile, activityStreamObject.getContent())) {

      log.info("Import File saved {} ", importJob.getName());
      importJobRepository.save(importJob);

      unzip(importJob);

      // Remove BOM
      executeStepWithImportFile(importJob, file -> !file.isError(), file -> fileService.removeBOM(file));
      importJobRepository.save(importJob);

      log.info("BOM Removed");

      // XML Format
      executeStepWithImportFile(importJob, file -> !file.isError(), file -> xmlService.applyXMLFormat(file));
      importJobRepository.save(importJob);

      log.info("XML Type analysed");

      // XSLT
      executeStepWithImportFile(importJob,
          file -> !file.isError(),
          file -> xmlService.xsltToTEI(file));
      importJobRepository.save(importJob);
      log.info("XSLT To TEI applied");

      executeXMLValidation(activityStreamObject, importJob);

      // Send to Kafka
      if (importJob.getImportFiles().stream().noneMatch(ImportFile::isError)) {

        importJob.setResult(TotalResult.IN_PROGRESS);
        kafkaNachweisProducer.sendMessage(importJob, internExternTag);

        log.info("Send Kafka Message");

      } else {
        log.error("Import[importDateien]: Job not send to Kafka (One or more xml files are not valid): {}",
            importJob.getName());
        importJob.setResult(TotalResult.FAILED);

        String details = importJob.getImportFiles().stream()
            .filter(ImportFile::isError)
            .map(importFile -> importFile.getDateiName() + ": " + importFile.getMessage())
            .collect(Collectors.joining("; "));

        importJob.setErrorMessage(
            messageSource.getMessage("beschreibungsimport.error.invalidfiles",
                new String[]{details}, LocaleContextHolder.getLocale()));
      }
    }

    importJobRepository.save(importJob);

    cleanUpImportDirectory(importJob);

    return importJob;

  }

  private void executeXMLValidation(ActivityStreamObject activityStreamObject, ImportJob result) {
    // XSD validation in case of TEI its relaxNG and Schematron validation
    log.info("XML Schema validated with TEI-ODD {} ", activityStreamObject.getTag()
        .stream().anyMatch(tag -> HSP_FORMAT_VALIDATION_AS_STRING.equals(tag.getId())));

    executeStepWithImportFile(result, file -> !file.isError(), file -> xmlService.validateSchema(file));

    Predicate<ImportFile> fileTypeTEI = file -> file.getDateiFormat().equals(DateiFormate.TEI_ALL);

    activityStreamObject.getTag().stream().filter(tag -> HSP_FORMAT_VALIDATION_AS_STRING.equals(tag.getId())).findFirst()
        .ifPresentOrElse((tag)-> {
          if("false".equals(tag.getName())) {
            executeStepWithImportFile(result, file -> !file.isError() && fileTypeTEI.test(file), file -> xmlService.hspValidateSchema(file));
          }
        }, ()-> executeStepWithImportFile(result, file -> !file.isError() && fileTypeTEI.test(file), file -> xmlService.hspValidateSchema(file)));

    importJobRepository.save(result);
  }

  @Value("${import.datadirectory}")
  public void setDataDirectory(String dataDirectory) {
    this.dataDirectory = dataDirectory;
  }

  @Autowired
  public void setXmlService(XMLService xmlService) {
    this.xmlService = xmlService;
  }

  @Autowired
  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  @Autowired
  public void setImportJobRepository(
      ImportJobRepository importJobRepository) {
    this.importJobRepository = importJobRepository;
  }
}

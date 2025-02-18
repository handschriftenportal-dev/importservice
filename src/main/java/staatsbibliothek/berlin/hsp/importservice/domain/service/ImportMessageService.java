package staatsbibliothek.berlin.hsp.importservice.domain.service;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 04.06.2020.
 * @version 1.0
 * <p>
 * This Class is in charge to start the proper import based on an ActivityStreamMessage
 */

@Component
@Scope("singleton")
public class ImportMessageService {

  public static final String EMPTY_MESSAGE = "Empty or illegal Message.";
  public static final String NO_TARGET_SERVICE = "No target service found.";
  public static final String NOT_SUPPORTED_ACTION = "Not supported import action.";
  public static final String UNKOWN = "Unkown";
  private final Logger logger = LoggerFactory.getLogger(ImportMessageService.class);
  @Getter
  @Qualifier("BeschreibungsImport")
  private final DateiImportBoundary beschreibungenImportService;

  @Qualifier("NormdatenImport")
  private final DateiImportBoundary normdatenImportService;

  @Qualifier("DigitalisateImport")
  private final DateiImportBoundary importDigitalisateService;

  @Qualifier("KatalogeImport")
  private final DateiImportBoundary katalogeImportService;

  @Getter
  private final ImportJobRepository importJobRepository;

  @Autowired
  public ImportMessageService(
      BeschreibungenImportService beschreibungenImportService,
      NormdatenImportService normdatenImportService,
      DigitalisateImportService digitalisateImportService,
      KatalogeImportService katalogeImportService,
      ImportJobRepository importJobRepository) {
    this.beschreibungenImportService = beschreibungenImportService;
    this.normdatenImportService = normdatenImportService;
    this.importJobRepository = importJobRepository;
    this.katalogeImportService = katalogeImportService;
    this.importDigitalisateService = digitalisateImportService;
  }

  public void handleMessage(ActivityStreamMessage message) {

    if (message != null) {

      if (message.getAction() != null) {
        switch (message.getAction()) {
          case ADD:
            logger.info("handle ADD Action {} ", message.getId());
            handleAddAction(message);
            break;
          case UPDATE:
            logger.info("handle UPDATE Action {} ", message.getId());
            handleUpdateAction(message);
            break;
          case REMOVE:
            logger.info("handle REMOVE Action {} ", message.getId());
          default:
            logger.info("handle DEFAULT Action {} ", message.getId());
            createAndSaveImportJobWithError(NOT_SUPPORTED_ACTION, message);
            break;
        }
      } else {
        createAndSaveImportJobWithError(EMPTY_MESSAGE, message);
      }

    } else {
      createAndSaveImportJobWithError(EMPTY_MESSAGE, message);
    }

  }

  void handleUpdateAction(ActivityStreamMessage message) {

    if (message != null && message.getObjects() != null) {
      message.getObjects().forEach(o -> {
        logger.info("Recieving objects {}", o.getName());

        if (o.getType() != null && o.getType().equals(ActivityStreamsDokumentTyp.IMPORT)) {
          handleImportUpdate(o, message);
        }
      });
    }
  }

  void handleImportUpdate(ActivityStreamObject o, ActivityStreamMessage message) {

    logger.info("Handle Update Import Job with Message ");

    try {

      ImportJob job = ObjectMapperFactory.getObjectMapper()
          .readValue(o.getContent(), ImportJob.class);

      logger.info("Got new Job {} ", job.toString());

      importJobRepository.save(job);

    } catch (ActivityStreamsException | IOException e) {
      logger.error("Error during update import job ", e);
      importJobRepository.findById(message.getId()).ifPresent(oldJob -> {
        oldJob.setResult(TotalResult.FAILED);
        oldJob.setErrorMessage(e.getMessage());
        importJobRepository.save(oldJob);
      });
    }

  }

  public void createAndSaveImportJobWithError(String errorMessage, ActivityStreamMessage message) {

    String jobName = Optional.ofNullable(message).map(m -> m.getTarget().getName()).orElse(UNKOWN);
    String nutzerName = Optional.ofNullable(message).map(m -> m.getActor().getName())
        .orElse(UNKOWN);

    Set<ImportFile> files = new HashSet<>();

    if (message != null && message.getObjects() != null) {
      files.addAll(message.getObjects().stream()
          .map(o -> ImportFile.builder().id(o.getId())
              .dateiName(o.getName())
              .path(null)
              .dateiTyp(o.getMediaType())
              .dateiFormat(DateiFormate.UNBEKANNT)
              .error(false)
              .message("")
              .build()
          )
          .toList());
    }

    ImportJob job = new ImportJob("", jobName, nutzerName,
        ActivityStreamsDokumentTyp.BESCHREIBUNG.name(),
        TotalResult.FAILED, errorMessage);
    job.getImportFiles().addAll(files);

    importJobRepository.save(job);

  }

  public void handleAddAction(ActivityStreamMessage message) {

    if (message != null && message.getObjects() != null) {
      message.getObjects().forEach(o -> {
        logger.info("Receiving objects {}", o.getName());
        DateiImportBoundary service = mapActivityStreamsDokumentTypToService(o.getType());

        if (service != null) {
          handleADD(o, message, service, o.getType());
        } else {
          logger.error("Error during send message can't find service");
          createAndSaveImportJobWithError(NO_TARGET_SERVICE, message);
        }

      });
    } else {
      createAndSaveImportJobWithError(EMPTY_MESSAGE, message);
    }
  }

  protected DateiImportBoundary mapActivityStreamsDokumentTypToService(
      ActivityStreamsDokumentTyp typ) {
    return switch (typ) {
      case BESCHREIBUNG -> beschreibungenImportService;
      case KONZEPT, ORT, KOERPERSCHAFT, PERSON, BEZIEHUNG, SPRACHE, ONTOLOGIE ->
          normdatenImportService;
      case DIGITALISAT -> importDigitalisateService;
      case KATALOG -> katalogeImportService;
      default -> null;
    };
  }

  private String extractBenutzernameFromMessage(ActivityStreamMessage message) {
    String benutzerName = "";

    if (message.getActor() != null && message.getActor().getName() != null) {
      benutzerName = message.getActor().getName();
    }
    return benutzerName;
  }

  public void handleADD(ActivityStreamObject object, ActivityStreamMessage message,
      DateiImportBoundary importBoundary, ActivityStreamsDokumentTyp typ) {

    try {
      String benutzerName = extractBenutzernameFromMessage(message);
      importBoundary
          .importDateien(object,
              object.getName(), benutzerName, typ);
    } catch (ActivityStreamsException e) {
      logger.error("Error during reviecing Beschreibung from Kafka.", e);
      createAndSaveImportJobWithError(e.getMessage(), message);
    }

  }
}

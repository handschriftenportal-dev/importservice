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
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 04.06.2020.
 * @version 1.0
 * <p>
 * This Class is in charge to start the proper import based on an ActivityStreamMessage
 */

@Component
@Scope("singleton")
public class KafkaImportMessageService {

  public static final String EMPTY_MESSAGE = "Empty or illegal Message.";
  public static final String NOT_SUPPORTED_ACTION = "Not supported import action.";
  public static final String UNKOWN = "Unkown";
  private Logger logger = LoggerFactory.getLogger(KafkaImportMessageService.class);
  @Qualifier("BeschreibungsImport")
  private DateiImportBoundary beschreibungenImportService;

  @Qualifier("NormdatenImport")
  private DateiImportBoundary normdatenImportService;

  @Qualifier("DigitalisateImport")
  private DateiImportBoundary importDigitalisateService;

  @Qualifier("KatalogeImport")
  private DateiImportBoundary katalogeImportService;

  private ImportJobRepository importJobRepository;

  @Autowired
  public KafkaImportMessageService(
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
      message.getObjects().stream().forEach(o -> {
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

      ImportJob job = ObjectMapperFactory.getObjectMapper().readValue(o.getContent(), ImportJob.class);

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
    String nutzerName = Optional.ofNullable(message).map(m -> m.getActor().getName()).orElse(UNKOWN);

    Set<ImportFile> files = new HashSet<>();

    if (message != null && message.getObjects() != null) {
      files.addAll(message.getObjects().stream()
          .map(o -> new ImportFile(o.getId(), o.getName(), null, o.getMediaType(), XMLFormate.UNBEKANNT, true, ""))
          .collect(Collectors.toList()));
    }

    ImportJob job = new ImportJob("", jobName, nutzerName, ActivityStreamsDokumentTyp.BESCHREIBUNG.name(),
        TotalResult.FAILED, errorMessage);
    job.getImportFiles().addAll(files);

    importJobRepository.save(job);

  }

  public void handleAddAction(ActivityStreamMessage message) {

    if (message != null && message.getObjects() != null) {
      message.getObjects().stream().forEach(o -> {
        logger.info("Recieving objects {}", o.getName());

        if (ActivityStreamsDokumentTyp.BESCHREIBUNG.equals(o.getType())) {
          handleADD(o, message, beschreibungenImportService, o.getType());
        }

        if (Stream.of(ActivityStreamsDokumentTyp.ORT, ActivityStreamsDokumentTyp.KOERPERSCHAFT,
                ActivityStreamsDokumentTyp.BEZIEHUNG, ActivityStreamsDokumentTyp.PERSON, ActivityStreamsDokumentTyp.SPRACHE)
            .anyMatch(t -> t.equals(o.getType()))) {
          handleADD(o, message, normdatenImportService, o.getType());
        }

        if (ActivityStreamsDokumentTyp.DIGITALISAT.equals(o.getType())) {
          handleADD(o, message, importDigitalisateService, o.getType());
        }

        if (ActivityStreamsDokumentTyp.KATALOG.equals(o.getType())) {
          handleADD(o, message, katalogeImportService, o.getType());
        }

      });
    } else {
      createAndSaveImportJobWithError(EMPTY_MESSAGE, message);
    }
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

  public DateiImportBoundary getBeschreibungenImportService() {
    return beschreibungenImportService;
  }

  public ImportJobRepository getImportJobRepository() {
    return importJobRepository;
  }
}

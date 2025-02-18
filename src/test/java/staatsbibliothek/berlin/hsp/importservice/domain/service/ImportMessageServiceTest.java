package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportEntityData;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 04.06.2020.
 * @version 1.0
 */

@SpringBootTest
public class ImportMessageServiceTest {

  @Autowired
  private ImportMessageService importMessageService;

  @MockBean
  private BeschreibungenImportService beschreibungenImportServiceMock;

  @MockBean
  private NormdatenImportService normdatenImportServiceMock;

  @MockBean
  private DigitalisateImportService digitalisateImportServiceMock;

  @MockBean
  private KatalogeImportService katalogeImportServiceMock;

  @Autowired
  private ImportJobRepository importJobRepository;

  @AfterEach
  void tearDown() {
    importJobRepository.deleteAll();
  }

  @BeforeEach
  void setup() {
    importJobRepository.deleteAll();
  }

  @Test
  void testCreation() {
    assertNotNull(importMessageService);

    assertNotNull(importMessageService.getBeschreibungenImportService());

    assertNotNull(importMessageService.getImportJobRepository());
  }

  @Test
  void testhandleMessage() throws ActivityStreamsException {

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    ActivityStreamObject activityStreamObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
        .withUrl("http://localhost")
        .withId("1")
        .withGroupId("beschreibung1")
        .withContent("Test".getBytes())
        .build();

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(activityStreamObject)
        .build();

    importMessageService.handleMessage(message);

    verify(beschreibungenImportServiceMock, times(1)).importDateien(any(), any(), any(), any());

  }

  @Test
  void testhandleMessageWithBadMessages() throws ActivityStreamsException {

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .withTargetName("test")
        .build();

    importMessageService.handleMessage(message);

    ImportJob importJob = importJobRepository.findAll().iterator().next();

    assertNotNull(importJob);

    assertEquals(message.getTarget().getName(), importJob.getName());

    assertEquals(message.getActor().getName(), importJob.getBenutzerName());

    assertEquals(ImportMessageService.EMPTY_MESSAGE, importJob.getErrorMessage());
  }

  @Test
  void testhandleMessageWithNullMessages() {

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    importMessageService.handleMessage(null);

    ImportJob importJob = importJobRepository.findAll().iterator().next();

    assertNotNull(importJob);

    assertEquals(ImportMessageService.EMPTY_MESSAGE, importJob.getErrorMessage());
  }

  @Test
  void testhandleMessageWithNotSupportedMessages() throws ActivityStreamsException {
    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withPublished(LocalDateTime.now())
        .withType(ActivityStreamAction.REMOVE)
        .withActorName("Konrad Eichstädt")
        .withTargetName("test")
        .build();

    importMessageService.handleMessage(message);

    ImportJob importJob = importJobRepository.findAll().iterator().next();

    assertNotNull(importJob);

    assertEquals(ImportMessageService.NOT_SUPPORTED_ACTION, importJob.getErrorMessage());
  }

  @ParameterizedTest
  @EnumSource(value = ActivityStreamsDokumentTyp.class, names = {"ORT", "BESCHREIBUNG", "KATALOG"})
  void testhandleAddAction(ActivityStreamsDokumentTyp typ) throws ActivityStreamsException {

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    ActivityStreamObject activityStreamObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(typ)
        .withId("1")
        .withName("Daten")
        .withContent("Test".getBytes())
        .build();

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(activityStreamObject)
        .build();

    importMessageService.handleAddAction(message);

    if (ActivityStreamsDokumentTyp.ORT.equals(typ)) {
      verify(normdatenImportServiceMock, times(1))
          .importDateien(activityStreamObject, activityStreamObject.getName(),
              message.getActor().getName(), typ);
    }

    if (ActivityStreamsDokumentTyp.BESCHREIBUNG.equals(typ)) {
      verify(beschreibungenImportServiceMock, times(1))
          .importDateien(activityStreamObject, activityStreamObject.getName(),
              message.getActor().getName(), typ);
    }

    if (ActivityStreamsDokumentTyp.KATALOG.equals(typ)) {
      verify(katalogeImportServiceMock, times(1))
          .importDateien(activityStreamObject, activityStreamObject.getName(),
              message.getActor().getName(), typ);
    }

  }

  @ParameterizedTest
  @EnumSource(value = ActivityStreamsDokumentTyp.class, names = {"ORT", "KOERPERSCHAFT",
      "BEZIEHUNG", "PERSON", "SPRACHE", "KONZEPT"})
  void testHandleNormdatenAdd(ActivityStreamsDokumentTyp typ) throws ActivityStreamsException {

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    ActivityStreamObject activityStreamObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(typ)
        .withId("1")
        .withName("Erste Normdaten")
        .withGroupId("beschreibung1")
        .withContent("Test".getBytes())
        .build();

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(activityStreamObject)
        .build();

    importMessageService.handleADD(activityStreamObject, message, normdatenImportServiceMock, typ);

    verify(normdatenImportServiceMock, times(1))
        .importDateien(activityStreamObject, activityStreamObject.getName(),
            message.getActor().getName(), typ);
  }

  @Test
  @Transactional
  void testhandleImportUpdate() throws ActivityStreamsException, JsonProcessingException {

    ImportJob job = new ImportJob("123", "", "TestJob", "Konrad");
    ImportJob jobFinished = new ImportJob("123", "", "TestJob", "Konrad");
    ImportFile importFile = ImportFile.builder().id("123").dateiName("")
        .path(null)
        .dateiTyp(null)
        .dateiFormat(null)
        .error(false)
        .message("")
        .build();
    importFile.getImportEntityData().add(new ImportEntityData("1", "", null));
    jobFinished.setResult(TotalResult.SUCCESS);
    jobFinished.getImportFiles().add(importFile);
    importJobRepository.save(job);

    ActivityStreamObject importObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.IMPORT)
        .withId("1")
        .withContent(ObjectMapperFactory.getObjectMapper().writeValueAsString(jobFinished))
        .build();

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withType(ActivityStreamAction.UPDATE)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(importObject)
        .build();

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    assertEquals(0, job.getImportFiles().size());
    assertEquals(TotalResult.NO_RESULT, job.getResult());
    assertTrue(importJobRepository.findById("123").isPresent());

    importMessageService.handleImportUpdate(importObject, message);

    Optional<ImportJob> updatedJob = importJobRepository.findById("123");

    assertTrue(updatedJob.isPresent());
    assertEquals(1, updatedJob.get().getImportFiles().size());
    for (ImportFile imported : updatedJob.get().getImportFiles()) {
      assertEquals(1, imported.getImportEntityData().size());
    }
    assertEquals(TotalResult.SUCCESS, updatedJob.get().getResult());
  }

  @Test
  @Transactional
  void testHandleUpdateImportJobFailure() throws ActivityStreamsException, JsonProcessingException {
    ImportJob job = new ImportJob("123", "", "TestJob", "Konrad");
    ImportJob jobFinished = new ImportJob("123", "", "TestJob", "Konrad");
    jobFinished.setResult(TotalResult.FAILED);
    jobFinished.setErrorMessage("Fehler");
    importJobRepository.save(job);

    ActivityStreamObject importObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.IMPORT)
        .withId("1")
        .withContent(ObjectMapperFactory.getObjectMapper().writeValueAsString(jobFinished))
        .build();

    ActivityStreamMessage message = (ActivityStreamMessage) ActivityStream
        .builder()
        .withId("123")
        .withType(ActivityStreamAction.UPDATE)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(importObject)
        .build();

    ImportMessageService importMessageService = new ImportMessageService(
        beschreibungenImportServiceMock,
        normdatenImportServiceMock, digitalisateImportServiceMock, katalogeImportServiceMock,
        importJobRepository);

    assertEquals(0, job.getImportFiles().size());
    assertEquals(TotalResult.NO_RESULT, job.getResult());
    assertTrue(importJobRepository.findById("123").isPresent());

    importMessageService.handleImportUpdate(importObject, message);

    Optional<ImportJob> updatedJob = importJobRepository.findById("123");

    assertTrue(updatedJob.isPresent());
    assertEquals(0, updatedJob.get().getImportFiles().size());
    assertEquals(TotalResult.FAILED, updatedJob.get().getResult());
    assertEquals("Fehler",
        updatedJob.get().getErrorMessage());
  }
}

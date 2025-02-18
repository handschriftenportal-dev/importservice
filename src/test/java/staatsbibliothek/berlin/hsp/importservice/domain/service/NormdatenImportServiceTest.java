package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNormdatenProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 05.06.2020.
 * @version 1.0
 */

@SpringBootTest
public class NormdatenImportServiceTest {

  @Autowired
  @Qualifier("NormdatenImport")
  private DateiImportBoundary normdatenImportService;

  @Autowired
  private XMLService xmlService;

  @Autowired
  private RDFService rdfService;

  @Autowired
  private FileService fileService;

  @MockBean
  private KafkaNormdatenProducer kafkaNormdatenProducerMock;

  @MockBean
  private ImportJobRepository importJobRepositoryMock;

  @Autowired
  private ImportJobConvert importJobConvert;

  @Test
  void testCreation() {
    assertNotNull(normdatenImportService);

    assertNotNull(((NormdatenImportService) normdatenImportService).getDataDirectory());

    assertNotNull(((NormdatenImportService) normdatenImportService).getFileService());

    assertNotNull(((NormdatenImportService) normdatenImportService).getXmlService());
  }

  @Test
  void testimportOrte()
      throws IOException, ParserConfigurationException, SAXException, ActivityStreamsException {

    String filename = "TEI_Import_Orte.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.ORT).withContent(content).build();

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    ImportJob job = service.importDateien(activityStreamObject, filename, "Konrad",
        ActivityStreamsDokumentTyp.ORT);

    assertNotNull(job);

    assertEquals(filename, job.getName());

    assertEquals("Konrad", job.getBenutzerName());

    assertEquals(1, job.getImportFiles().size());

    assertEquals(DateiFormate.TEI_ALL, xmlService.identifyXMLFormat(file));

    verify(kafkaNormdatenProducerMock, times(1)).sendMessage(any());

    verify(importJobRepositoryMock, times(5)).save(job);

    for (ImportFile f : job.getImportFiles()) {
      assertEquals(DateiFormate.TEI_ALL, f.getDateiFormat());

      assertEquals(false, f.isError());

    }

  }

  @Test
  void testimportKoerperschaft()
      throws IOException, ParserConfigurationException, SAXException, ActivityStreamsException {

    String filename = "TEI_Import_Koerperschaften_Partner.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.KOERPERSCHAFT).withContent(content).build();

    ImportJob job = service.importDateien(activityStreamObject, filename, "Konrad",
        ActivityStreamsDokumentTyp.KOERPERSCHAFT);

    assertNotNull(job);

    assertEquals(filename, job.getName());

    assertEquals("Konrad", job.getBenutzerName());

    assertEquals(1, job.getImportFiles().size());

    assertEquals(DateiFormate.TEI_ALL, xmlService.identifyXMLFormat(file));

    verify(kafkaNormdatenProducerMock, times(1)).sendMessage(any());

    verify(importJobRepositoryMock, times(5)).save(job);

    for (ImportFile datei : job.getImportFiles()) {
      assertEquals(DateiFormate.TEI_ALL, datei.getDateiFormat());

      assertFalse(datei.isError());

    }

  }

  @Test
  void testimportSprachen()
      throws IOException, ParserConfigurationException, SAXException, ActivityStreamsException {

    String filename = "TEI_Import_Sprachen.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.SPRACHE).withContent(content).build();

    ImportJob job = service.importDateien(activityStreamObject, filename, "Konrad",
        ActivityStreamsDokumentTyp.SPRACHE);

    assertNotNull(job);

    assertEquals(filename, job.getName());

    assertEquals("Konrad", job.getBenutzerName());

    assertEquals(1, job.getImportFiles().size());

    assertEquals(DateiFormate.TEI_ALL, xmlService.identifyXMLFormat(file));

    verify(kafkaNormdatenProducerMock, times(1)).sendMessage(any());

    verify(importJobRepositoryMock, times(5)).save(job);

    for (ImportFile f : job.getImportFiles()) {
      assertEquals(DateiFormate.TEI_ALL, f.getDateiFormat());

      assertEquals(false, f.isError());

    }

  }

  @Test
  void testImportRDFTurtleKonzepte() throws IOException, ActivityStreamsException {
    String filename = "hsp_SKOS_Import_Digitalisierung.ttl";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.KONZEPT).withContent(content).build();

    ImportJob job = service.importDateien(activityStreamObject, filename, "Konrad",
        ActivityStreamsDokumentTyp.KONZEPT);

    assertNotNull(job);

    assertEquals("KONZEPT", job.getDatatype());
    assertEquals(1, job.getImportFiles().size());

    job.getImportFiles().stream().findFirst().ifPresent(datei -> {
      assertEquals("text/turtle", datei.getDateiTyp());
      assertFalse(datei.isError());
      assertEquals("", datei.getMessage());
    });

    verify(kafkaNormdatenProducerMock, times(1)).sendMessage(any());
  }

  @Test
  void testmapFileToMessage() throws IOException, ActivityStreamsException {

    String filename = "TEI_Import_Orte.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    ImportJob importJob = new ImportJob(file.getAbsolutePath(), filename, "Konrad");
    ImportFile importFile = ImportFile.builder().id("123")
        .dateiName(filename)
        .path(file.toPath())
        .dateiTyp("application/xml")
        .dateiFormat(DateiFormate.UNBEKANNT).error(false)
        .message("")
        .build();

    ActivityStream activityStream = service.mapFileToMessage(importFile, importJob,
        ActivityStreamsDokumentTyp.ORT);

    assertNotNull(activityStream);

    assertEquals(importJob.getId(), activityStream.getId());

    assertEquals(ActivityStreamAction.ADD, activityStream.getAction());

    assertEquals(importJob.getBenutzerName(), activityStream.getActor().getName());

    assertEquals(importJob.getName(), activityStream.getTarget().getName());

    assertEquals(2, activityStream.getObjects().size());

    for (ActivityStreamObject activityStreamObject : activityStream.getObjects()) {

      if (activityStreamObject.getType().equals(ActivityStreamsDokumentTyp.ORT)) {
        assertEquals(ActivityStreamsDokumentTyp.ORT, activityStreamObject.getType());

        assertEquals(content.length, activityStreamObject.getContent().length);

        assertEquals(filename, activityStreamObject.getName());

        assertEquals(importFile.getDateiTyp(), activityStreamObject.getMediaType());
      }
      if (activityStreamObject.getType().equals(ActivityStreamsDokumentTyp.IMPORT)) {
        assertEquals(ActivityStreamsDokumentTyp.IMPORT, activityStreamObject.getType());

        assertEquals(importJobConvert.toJson(importJob),
            new String(activityStreamObject.getContent(), StandardCharsets.UTF_8));

        assertEquals(importJob.getName(), activityStreamObject.getName());

        assertEquals("application/json", activityStreamObject.getMediaType());
      }

    }
  }

  @Test
  void testsendKafkaMessage() throws IOException {

    ImportJob importJob = createTestImportJob();

    ImportFile importFile = ImportFile.builder().id("123")
        .dateiName(importJob.getName())
        .path(Paths.get(importJob.getImportDir()))
        .dateiTyp("application/xml")
        .dateiFormat(DateiFormate.TEI_ALL).error(false)
        .message("")
        .build();

    NormdatenImportService service = new NormdatenImportService("/tmp", xmlService, fileService,
        kafkaNormdatenProducerMock,
        importJobRepositoryMock, importJobConvert, rdfService);

    service.sendKafkaMessage(importFile, importJob, ActivityStreamsDokumentTyp.ORT);

    verify(kafkaNormdatenProducerMock, times(1)).sendMessage(any());
  }

  private ImportJob createTestImportJob() throws IOException {
    String filename = "TEI_Import_Orte.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);

    ImportJob importJob = new ImportJob(file.getAbsolutePath(), filename, "Konrad");
    return importJob;
  }
}

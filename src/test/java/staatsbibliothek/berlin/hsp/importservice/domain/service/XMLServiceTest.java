package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static java.nio.file.Files.newInputStream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.staatsbibliothek.berlin.hsp.messaging.objectfactory.TEIObjectFactory;
import jakarta.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import org.tei_c.ns._1.TEI;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.ValidationResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.ValidationResult.ValidationDetails;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 01.04.2019.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class XMLServiceTest {

  private static final Logger logger = LoggerFactory.getLogger(XMLServiceTest.class);

  @Autowired
  private XMLService xmlService;

  @Value("${import.datadirectory}")
  private String dataDirectory;

  @Value("${checkproductiondata}")
  private Boolean checkproductiondata;

  @Test
  void testidentifyXMLFormat() throws Exception {
    File teiFile = ResourceUtils.getFile("classpath:alto-B_SB_Kat_6_1_0001-IDs-TEI.xml");

    assertEquals(DateiFormate.TEI_ALL, xmlService.identifyXMLFormat(teiFile));

    File marcFile = ResourceUtils.getFile("classpath:BV021690266.xml");

    assertEquals(DateiFormate.MARC21, xmlService.identifyXMLFormat(marcFile));

    File hidaFile = ResourceUtils.getFile("classpath:SBB_Ms_germ_oct_115.xml");

    assertEquals(DateiFormate.MXML, xmlService.identifyXMLFormat(hidaFile));
  }

  @Test
  void testDetectXMLFormat() throws Exception {
    ImportJob job = createFileAndJob("BV021690266.xml");

    xmlService.applyXMLFormat(job.getImportFiles().iterator().next());
    assertEquals(DateiFormate.MARC21, job.getImportFiles().iterator().next().getDateiFormat());

    job = createFileAndJob("MXMLDokument.xml");

    xmlService.applyXMLFormat(job.getImportFiles().iterator().next());
    assertEquals(DateiFormate.MXML, job.getImportFiles().iterator().next().getDateiFormat());

    job = createFileAndJob("tei_msDesc_Heinemann.xml");

    xmlService.applyXMLFormat(job.getImportFiles().iterator().next());
    assertEquals(DateiFormate.TEI_ALL, job.getImportFiles().iterator().next().getDateiFormat());

    Files.delete(new File(dataDirectory + File.separator + "BV021690266.xml").toPath());
    Files.delete(new File(dataDirectory + File.separator + "MXMLDokument.xml").toPath());
    Files.delete(new File(dataDirectory + File.separator + "tei_msDesc_Heinemann.xml").toPath());
  }

  @Test
  void testTranslate_MXML() throws Exception {
    final File inFile = new File(dataDirectory + File.separator + "MXMLDokument.xml");
    Files.copy(ResourceUtils.getFile("classpath:" + "MXMLDokument.xml").toPath(), inFile.toPath(),
        StandardCopyOption.REPLACE_EXISTING);

    final File outFile = new File(dataDirectory + File.separator + "test.xml");
    try (InputStream in = new FileInputStream(inFile); OutputStream out = new FileOutputStream(
        outFile)) {
      xmlService.translate(in, out, DateiFormate.MXML);

      assertTrue(outFile.exists());
      assertTrue(outFile.length() > 0);
    } finally {
      Files.delete(inFile.toPath());
      Files.delete(outFile.toPath());
    }
  }

  @Test
  void testTranslate_TEI() throws Exception {
    final File inFile = new File(dataDirectory + File.separator + "tei-msDesc_hsp_bare.xml");
    Files.copy(ResourceUtils.getFile("classpath:" + "tei-msDesc_hsp_bare.xml").toPath(),
        inFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

    final File outFile = new File(dataDirectory + File.separator + "test_tei.xml");
    try (InputStream in = new FileInputStream(inFile); OutputStream out = new FileOutputStream(
        outFile)) {
      xmlService.translate(in, out, DateiFormate.TEI_ALL);

      assertTrue(outFile.exists());
      assertTrue(outFile.length() > 0);
    } finally {
      Files.delete(inFile.toPath());
      Files.delete(outFile.toPath());
    }
  }

  @Test
  void testValidateXSD() throws Exception {
    ImportJob job = createFileAndJob("tei_msDesc_Heinemann.xml");
    job.getImportFiles().iterator().next().setDateiFormat(DateiFormate.TEI_ALL);

    xmlService.validateSchema(job.getImportFiles().iterator().next());

    assertTrue(job.getImportFiles().iterator().next().isError());

    for (ImportFile importFile : job.getImportFiles()) {
      final File deleteFile = importFile.getPath().toFile();
      Files.delete(deleteFile.toPath());
    }

    job = createFileAndJob("tei-msDesc_Lesser-wthout-BOM44.xml");
    job.getImportFiles().iterator().next().setDateiFormat(DateiFormate.TEI_ALL);

    xmlService.validateSchema(job.getImportFiles().iterator().next());

    assertFalse(job.getImportFiles().iterator().next().isError());

    for (ImportFile importFile : job.getImportFiles()) {
      Files.delete(importFile.getPath());
    }
  }

  @Test
  void testHspValidateSchema() throws Exception {
    ImportJob job = createFileAndJob("tei-msDesc_Lesser-wthout-BOM44.xml");
    job.getImportFiles().iterator().next().setDateiFormat(DateiFormate.TEI_ALL);

    xmlService.hspValidateSchema(job.getImportFiles().iterator().next());

    assertTrue(job.getImportFiles().iterator().next().isError());

    for (ImportFile importFile : job.getImportFiles()) {
      final File deleteFile = importFile.getPath().toFile();
      Files.delete(deleteFile.toPath());
    }

    job = createFileAndJob("tei-msDesc_hsp_bare.xml");
    job.getImportFiles().iterator().next().setDateiFormat(DateiFormate.TEI_ALL);

    xmlService.hspValidateSchema(job.getImportFiles().iterator().next());

    ImportFile bla = job.getImportFiles().iterator().next();
    assertFalse(bla.isError());

    for (ImportFile importFile : job.getImportFiles()) {
      Files.delete(importFile.getPath());
    }
  }

  private ImportJob createFileAndJob(final String filename) throws Exception {
    ImportJob result;

    final File file = new File(dataDirectory + File.separator + filename);
    Files.copy(ResourceUtils.getFile("classpath:" + filename).toPath(), file.toPath(),
        StandardCopyOption.REPLACE_EXISTING);

    result = new ImportJob(dataDirectory, "test.zip", "test");
    result.getImportFiles().add(
        ImportFile.builder().id(UUID.randomUUID().toString()).dateiName("test.zip")
            .path(file.toPath()).dateiFormat(DateiFormate.UNBEKANNT).error(false).message("")
            .build());

    return result;
  }

  @Test
  void testxsltToTEI() throws IOException {

    if (checkproductiondata) {
      Path resourceDirectory = Paths.get("src", "test", "resources", "mxml_version_1_0");
      Stream<Path> list = Files.list(resourceDirectory);

      List<ImportFile> importFiles = new ArrayList<>();

      for (Path file : list.collect(Collectors.toList())) {

        String dateiName = file.getFileName().toString();
        ImportFile importFile = ImportFile.builder().id(UUID.randomUUID().toString())
            .dateiName(dateiName).path(file).dateiTyp("MXML").dateiFormat(DateiFormate.MXML)
            .error(false).message("").build();

        logger.info("Import[xsltToTEI]: Start test processing for the file: {}", dateiName);

        xmlService.xsltToTEI(importFile);
        xmlService.validateSchema(importFile);
        importFiles.add(importFile);
      }

      for (ImportFile f : importFiles) {
        Files.delete(f.getPath());
      }

      assertTrue(importFiles.stream()
          .allMatch(file -> DateiFormate.TEI_ALL.equals(file.getDateiFormat())));

      importFiles.forEach(file -> assertFalse(file.isError(),
          "Production Data file " + file.getDateiName()
              + " can't be transformed by XSLT MXML to TEI Script!"));
    }
  }

  @Test
  void testxsltToTEI_TEI2TEI() throws IOException {

    Path teiPath = Paths.get("src", "test", "resources", "tei-msDesc_Koch.xml");

    List<ImportFile> importFiles = new ArrayList<>();

    String dateiName = teiPath.getFileName().toString();
    ImportFile importFile = ImportFile.builder().id(UUID.randomUUID().toString())
        .dateiName(dateiName).path(teiPath).dateiTyp("TEI").dateiFormat(DateiFormate.TEI_ALL)
        .error(false).message("").build();

    logger.info("Import[xsltToTEI]: Start test processing for the file: {}", dateiName);

    xmlService.xsltToTEI(importFile);
    xmlService.validateSchema(importFile);

    Files.delete(importFile.getPath());

    assertTrue(
        importFiles.stream().allMatch(file -> DateiFormate.TEI_ALL.equals(file.getDateiFormat())));
    assertTrue(importFiles.stream().allMatch(file -> Boolean.valueOf(false).equals(file.isError())),
        "Data can't be transformed by XSLT TEI to TEI Script!");
  }


  @Test
  void testValidateTEIXML() throws IOException, SAXException {

    Path teiFilePath = Paths.get("src", "test", "resources", "tei-msDesc_Lesser-wthout-BOM44.xml");

    assertTrue(xmlService.validate(Files.readString(teiFilePath)));

  }

  @Test
  void testValidateWithODDTEIXML() throws IOException, SAXException {

    Path teiFilePath = Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare.xml");

    assertTrue(xmlService.validateWithODD(Files.readString(teiFilePath)));

  }

  @Test
  void testValidateWithInvalideTEIXML() throws IOException, JAXBException {

    Path teiFilePath = Paths.get("src", "test", "resources", "tei-msDesc_Westphal_invalide.xml");

    try (InputStream inputStream = newInputStream(teiFilePath)) {
      List<TEI> tei = TEIObjectFactory.unmarshal(inputStream);

      if (tei.get(0) == null) {
        assertThrows(SAXParseException.class,
            () -> xmlService.validate(Files.readString(teiFilePath)));
      }
    }
  }

  @Test
  void testValidateWithODDWithInvalideTEIXML() throws IOException, JAXBException {

    Path teiFilePath = Paths.get("src", "test", "resources", "tei-msDesc_Westphal_invalide.xml");

    try (InputStream inputStream = newInputStream(teiFilePath)) {
      List<TEI> tei = TEIObjectFactory.unmarshal(inputStream);

      if (tei.get(0) == null) {
        assertThrows(SAXParseException.class,
            () -> xmlService.validateWithODD(Files.readString(teiFilePath)));
      }
    }
  }

  @Test
  void testvalidateXMLUsingSchematron() throws Exception {
    Path teiFilePath = Paths.get("src", "test", "resources", "tei_wrongSchema.xml");

    final ValidationResult validationResult = xmlService.validateXMLUsingSchematron(
        Files.readString(teiFilePath));
    assertFalse(validationResult.isValid());
    assertEquals(2, validationResult.getDetails().size());
    ValidationDetails validationDetails = validationResult.getDetails().get(0);
    String requredContentOfValidationDetailsError = "requires a date in the standard form: yyyy-mm-dd.";
    assertTrue(validationDetails.getError().contains(requredContentOfValidationDetailsError),
        "'" + validationDetails.getError() + "does not contains '"
            + requredContentOfValidationDetailsError + "'");
    assertEquals(
        "/*:TEI[1]/*:text[1]/*:body[1]/*:msDesc[1]/*:additional[1]/*:surrogates[1]/*:bibl[1]/*:date[1]",
        validationDetails.getXpath());

    assertEquals(2, validationResult.getDetails().get(1).getDiagnostics().size());

    assertEquals(
        "\n"
            + "The date '149x' given in the 'term' element must be formated as an ISO date! -- Please format the date entry according to one of the following patterns: YYYY-MM-DD, -YYYY-MM-DD, YYYY-MM , -YYYY-MM or YYYY.",
        validationResult.getDetails().get(1).getDiagnostics().get(0).getMessage());

    assertEquals("en",
        validationResult.getDetails().get(1).getDiagnostics().get(0).getLanguageCode());
  }

  @Test
  void testValidateWithInvalideKodId() {

    Path teiFilePath = Paths.get("src", "test", "resources", "tei-msDesc_kodId_invalid.xml");

    SAXParseException saxParseException = assertThrows(SAXParseException.class,
        () -> xmlService.validate(Files.readString(teiFilePath)));

    assertEquals("ID \"HSP-3caaab5b-50bc-3e2b-93bb-99d378b849d9\" has already been defined",
        saxParseException.getMessage());
  }

}

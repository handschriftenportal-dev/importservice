package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import org.apache.jena.riot.RDFLanguages;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 12.01.24.
 * @version 1.0
 */

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class RDFServiceTest {

  @Inject
  private RDFService rdfService;

  @Test
  void testFindRDFformat() throws FileNotFoundException {

    assertNotNull(rdfService);

    File rdfTurtleFile = ResourceUtils.getFile("classpath:hsp_SKOS_Import_Digitalisierung.ttl");

    assertNotNull(rdfTurtleFile);

    ImportFile importFile = ImportFile.builder().id("1")
        .dateiName("Concept")
        .path(Path.of(rdfTurtleFile.getAbsolutePath()))
        .dateiTyp("text/turtle")
        .dateiFormat(null).error(false)
        .message("")
        .build();

    rdfService.applyRDFFormat(importFile);

    assertEquals(DateiFormate.RDF_TURTLE, importFile.getDateiFormat());
  }

  @Test
  void testBadInputforApplyRDFFormat() {
    assertThrows(IllegalArgumentException.class, () -> rdfService.applyRDFFormat(null));
  }

  @Test
  void testValidateFormat() throws FileNotFoundException {

    ImportJob importJob = new ImportJob("/tmp", "Test", "b-ke101", "KONZEPT",
        TotalResult.IN_PROGRESS, "");

    File rdfTurtleFile = ResourceUtils.getFile("classpath:hsp_SKOS_Import_Digitalisierung.ttl");
    ImportFile importFile = ImportFile.builder().id("1")
        .dateiName("Concept")
        .path(Path.of(rdfTurtleFile.getAbsolutePath()))
        .dateiTyp("text/turtle")
        .dateiFormat(DateiFormate.RDF_TURTLE).error(false)
        .message("")
        .build();

    rdfService.validateSchema(importFile, importJob);

    assertFalse(importFile.isError());

    assertEquals("", importFile.getMessage());
  }

  @Test
  void testInValidateFormat() throws FileNotFoundException {

    ImportJob importJob = new ImportJob("/tmp", "Test", "b-ke101", "KONZEPT",
        TotalResult.IN_PROGRESS, "");

    File rdfTurtleFile = ResourceUtils.getFile(
        "classpath:hsp_SKOS_Import_Digitalisierung_invalid.ttl");
    ImportFile importFile = ImportFile.builder().id("1")
        .dateiName("Concept")
        .path(Path.of(rdfTurtleFile.getAbsolutePath()))
        .dateiTyp("text/turtle")
        .dateiFormat(DateiFormate.RDF_TURTLE).error(false)
        .message("")
        .build();

    rdfService.validateSchema(importFile, importJob);

    assertTrue(importFile.isError());

    assertEquals("[line: 36, col: 33] Keyword 'a' not legal at this point",
        importFile.getMessage());
  }

  @Test
  void testisTurtle() {
    assertTrue(RDFService.isTurtle(RDFLanguages.filenameToLang("test.ttl")));
  }
}

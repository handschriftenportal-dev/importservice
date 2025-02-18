package staatsbibliothek.berlin.hsp.importservice.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 22.03.22
 */

@SpringBootTest
public class SchemaResourceFileRepositoryTest {

  @Autowired
  private SchemaResourceFileRepository schemaResourceFileRepository;

  @Test
  void testFindAll() {
    Iterable<SchemaResourceFile> result = schemaResourceFileRepository.findAll();

    assertNotNull(result);
    assertEquals(16, StreamSupport.stream(result.spliterator(), false).count());
  }

  @Test
  void testFindById() {
    Optional<SchemaResourceFile> result = schemaResourceFileRepository.findById(
        "90d6c42a-6cf8-3fd6-b28b-de8493f20734");

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals("90d6c42a-6cf8-3fd6-b28b-de8493f20734", result.get().getId());
    assertEquals("MXML-to-TEI-P5.xsl", result.get().getDateiName());
    assertEquals(SchemaResourceTyp.XSLT, result.get().getSchemaResourceTyp());
    assertEquals(DateiFormate.MXML, result.get().getXmlFormat());
    assertEquals("system", result.get().getBearbeitername());
    assertNotNull(result.get().getDatei());
    assertFalse(result.get().getDatei().isPresent());
    assertNotNull(result.get().getErstellungsDatum());
    assertNotNull(result.get().getAenderungsDatum());
  }
}

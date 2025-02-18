package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 02.09.22
 */

public class SchemaResourceFileTest {

  @Test
  void testCreation() {

    String id = "id";
    DateiFormate xmlFormat = DateiFormate.MXML;
    SchemaResourceTyp schemaResourceTyp = SchemaResourceTyp.XSLT;
    String dateiName = "datei.name";
    String bearbeitername = "system";
    Version version = Version.parse("1.2.3");
    LocalDateTime erstellungsDatum = LocalDateTime.now();
    LocalDateTime aenderungsDatum = LocalDateTime.now();

    SchemaResourceFile schemaResourceFile = SchemaResourceFile.builder()
        .withId(id)
        .withXmlFormat(xmlFormat)
        .withSchemaResourceTyp(schemaResourceTyp)
        .withDateiName(dateiName)
        .withBearbeitername(bearbeitername)
        .withVersion(Version.parse("1.2.3"))
        .withErstellungsDatum(erstellungsDatum)
        .withAenderungsDatum(aenderungsDatum)
        .withDatei(Optional.empty())
        .build();

    assertNotNull(schemaResourceFile);
    assertEquals(id, schemaResourceFile.getId());
    assertEquals(xmlFormat, schemaResourceFile.getXmlFormat());
    assertEquals(schemaResourceTyp, schemaResourceFile.getSchemaResourceTyp());
    assertEquals(dateiName, schemaResourceFile.getDateiName());
    assertEquals(bearbeitername, schemaResourceFile.getBearbeitername());
    assertEquals(version, schemaResourceFile.getVersion());
    assertEquals(erstellungsDatum, schemaResourceFile.getErstellungsDatum());
    assertEquals(aenderungsDatum, schemaResourceFile.getAenderungsDatum());
    assertNotNull(schemaResourceFile.getDatei());
    assertFalse(schemaResourceFile.getDatei().isPresent());
  }
}

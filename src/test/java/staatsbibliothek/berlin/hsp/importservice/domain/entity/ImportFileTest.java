package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */
public class ImportFileTest {

  @Test
  void testCreation() {
    ImportFile result = ImportFile.builder().id(UUID.randomUUID().toString()).dateiName("test.xml")
        .path(new File("text.xml").toPath())
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(true).message("Success")
        .build();

    Assertions.assertEquals("text.xml", result.getPath().toFile().getName());
    Assertions.assertEquals("application/xml", result.getDateiTyp());
    Assertions.assertEquals(DateiFormate.MARC21, result.getDateiFormat());
    Assertions.assertEquals(true, result.isError());
  }

  @Test
  void testEquals() {
    ImportFile result = ImportFile.builder().id(UUID.randomUUID().toString()).dateiName("test.xml")
        .path(new File("text.xml").toPath())
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(true).message("Success")
        .build();

    ImportFile result1 = ImportFile.builder().id(UUID.randomUUID().toString()).dateiName("test.xml")
        .path(new File("text.xml").toPath())
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(true).message("Success")
        .build();

    ImportFile result3 = ImportFile.builder().id(UUID.randomUUID().toString()).dateiName("test.xml")
        .path(new File("Bonn.xml").toPath())
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(true).message("Success")
        .build();

    Assertions.assertNotEquals(result, result1);

    Assertions.assertNotEquals(result, result3);
  }

}

package staatsbibliothek.berlin.hsp.importservice.domain.aggregate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportEntityData;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.service.ImportJobConvert;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 04.04.2019.
 */

@SpringBootTest
public class ImportJobTest {

  @Autowired
  private ImportJobConvert importJobConvert;

  @Test
  void testConstruction() {

    ImportJob importJob = new ImportJob("/tmp", "testjob", "Konrad");

    assertNotNull(importJob);
    assertNotNull(importJob.getImportFiles());
  }


  @Test
  void testtoJson() throws JsonProcessingException, MalformedURLException {

    final String result = "{\"id\":\"123\",\"creationDate\":\"2020-06-08T00:00:00\",\"benutzerName\":\"Konrad\",\"importFiles\":[{\"id\":\"345\",\"path\":null,\"dateiTyp\":\"application/xml\",\"dateiName\":\"test.xml\",\"dateiFormat\":\"MARC21\",\"error\":false,\"message\":\"\",\"importEntityData\":[{\"id\":\"12\",\"label\":\"Ort München\",\"url\":\"https://localhost:9090/orte/123\"}]}],\"name\":\"test.xml\",\"importDir\":\"/tmp\",\"result\":\"NO_RESULT\",\"errorMessage\":\"\",\"datatype\":\"ORT\"}";

    ImportFile file = ImportFile.builder().id("345").dateiName("test.xml").path(null)
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(false).message("")
        .build();

    ImportEntityData importEntityData = new ImportEntityData("12", "Ort München",
        new URL("https://localhost:9090/orte/123"));

    ImportJob importJob = new ImportJob("/tmp", "test.xml", "Konrad",
        ActivityStreamsDokumentTyp.ORT.name(),
        TotalResult.NO_RESULT, "");
    importJob.getImportFiles().addAll(Arrays.asList(file));
    file.getImportEntityData().addAll(Arrays.asList(importEntityData));
    importJob.setId("123");
    importJob.setCreationDate(LocalDateTime.of(2020, 6, 8, 00, 00, 00));

    String json = importJobConvert.toJson(importJob);

    assertNotNull(json);

    assertEquals(result, json);
  }

  @Test
  void testToString() {

    ImportFile file = ImportFile.builder().id("345").dateiName("test.xml").path(null)
        .dateiTyp("application/xml").dateiFormat(DateiFormate.MARC21).error(false).message("")
        .build();

    ImportJob importJob = new ImportJob("/tmp", "test.xml", "Konrad",
        ActivityStreamsDokumentTyp.ORT.name(),
        TotalResult.NO_RESULT, "");
    importJob.getImportFiles().addAll(List.of(file));
    importJob.setId("123");
    importJob.setCreationDate(LocalDateTime.of(2020, 6, 8, 00, 00, 00));

    Assertions.assertEquals(
        "ImportJob{id='123', creationDate=2020-06-08T00:00, benutzerName='Konrad', name='test.xml', importDir='/tmp', result=Kein Ergebnis, errorMessage='', datatype='ORT'}",
        importJob.toString());
  }
}

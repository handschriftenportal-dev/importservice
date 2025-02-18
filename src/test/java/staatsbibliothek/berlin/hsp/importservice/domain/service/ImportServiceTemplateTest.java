package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 30.09.2020.
 * @version 1.0
 */
public class ImportServiceTemplateTest {

  @Test
  void testCleanImportDirectory(@TempDir Path tempDir) throws IOException {

    final ImportJob job = new ImportJob(tempDir.toString() + "/test", "test.test", "test");
    String jobName = job.getName();

    Files.createDirectory(Paths.get(tempDir.toString(), "test"));

    File importFile = new File(job.getImportDir() + File.separator + jobName);

    final ImportFile initialImportFile = ImportFile.builder().id(UUID.randomUUID().toString())
        .dateiName(jobName)
        .path(importFile.toPath())
        .dateiFormat(DateiFormate.UNBEKANNT).error(false)
        .message(null)
        .build();

    job.getImportFiles().add(initialImportFile);

    Files.write(initialImportFile.getPath(), "Test".getBytes());

    Assertions.assertTrue(Files.exists(importFile.toPath()));

    ImportServiceTemplate.cleanUpImportDirectory(job);

    Assertions.assertFalse(Files.exists(importFile.toPath()));
  }

}

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
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

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

    final ImportFile initialImportFile = new ImportFile(UUID.randomUUID().toString(), jobName,
        importFile.toPath(), null,
        XMLFormate.UNBEKANNT, false,
        null);

    job.getImportFiles().add(initialImportFile);

    Files.write(initialImportFile.getPath(), "Test".getBytes());

    Assertions.assertTrue(Files.exists(importFile.toPath()));

    ImportServiceTemplate.cleanUpImportDirectory(job);

    Assertions.assertFalse(Files.exists(importFile.toPath()));
  }

}

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNachweisProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

@SpringBootTest
public class KatalogeImportServiceTest {

  @Autowired
  @Qualifier("KatalogeImport")
  private DateiImportBoundary importBoundary;

  @Autowired
  private XMLService xmlService;

  @Autowired
  private FileService fileService;

  @MockBean
  private ImportJobRepository importJobRepositoryMock;

  @Autowired
  private ImportJobConvert importJobConvert;

  @MockBean
  private KafkaNachweisProducer kafkaNachweisProducer;

  @Test
  void testCreation() {
    Assertions.assertNotNull(importBoundary);
  }

  @Test
  void testImportDateien() throws IOException, ActivityStreamsException {

    String filename = "katalog.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    KatalogeImportService service = new KatalogeImportService("/tmp", xmlService, fileService,
        kafkaNachweisProducer, importJobRepositoryMock, importJobConvert);

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.KOD)
        .withContent(content)
        .build();

    ImportJob job = service.importDateien(activityStreamObject, filename, "Benutzer", ActivityStreamsDokumentTyp.KATALOG);

    assertNotNull(job);

    verify(importJobRepositoryMock,times(5)).save(job);

    verify(kafkaNachweisProducer,times(1)).sendMessageWithActivityStream(any());
  }
}

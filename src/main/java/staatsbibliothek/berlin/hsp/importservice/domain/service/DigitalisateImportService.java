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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNachweisProducer;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 20.11.2020.
 * @version 1.0
 */

@Component
@Slf4j
@Qualifier("DigitalisateImport")
public class DigitalisateImportService extends ImportServiceTemplate {

  KafkaNachweisProducer kafkaNachweisProducer;

  public DigitalisateImportService(@Value("${import.datadirectory}") String dataDirectory,
      XMLService xmlService, FileService fileService,
      KafkaNachweisProducer kafkaNachweisProducer,
      ImportJobRepository importJobRepository, ImportJobConvert importJobConvert) {
    this.dataDirectory = dataDirectory;
    this.xmlService = xmlService;
    this.fileService = fileService;
    this.kafkaNachweisProducer = kafkaNachweisProducer;
    this.importJobRepository = importJobRepository;
    this.importJobConvert = importJobConvert;
  }

  @Override
  public ImportJob importDateien(ActivityStreamObject activityStreamObject, String fileName, String benutzerName,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) throws ActivityStreamsException {

    ImportJob importJob = initialise(fileName, benutzerName, activityStreamsDokumentTyp);
    final ImportFile initialImportFile = createImportFile(fileName, importJob);

    if (fileService.save(initialImportFile, activityStreamObject.getContent())) {

      unzip(importJob);

      identifyXMLFormatAndValidate(importJob);

      importJob.getImportFiles().stream().filter(ImportFile::isError).findFirst().ifPresentOrElse((f) -> {

        importJob.setResult(TotalResult.FAILED);
        importJob.setErrorMessage("One ore more files are failed with " + f.getMessage());
      }, () -> executeStepWithImportFile(importJob, file -> !file.isError(),
          file -> sendKafkaMessage(file, importJob, activityStreamsDokumentTyp)));
    }

    importJobRepository.save(importJob);

    cleanUpImportDirectory(importJob);

    return importJob;
  }

  private void sendKafkaMessage(ImportFile file, ImportJob importJob,
      ActivityStreamsDokumentTyp activityStreamsDokumentTyp) {

    try {
      kafkaNachweisProducer
          .sendMessageWithActivityStream(mapFileToMessage(file, importJob, activityStreamsDokumentTyp));
    } catch (IOException | ActivityStreamsException e) {
      log.error("Error during sending kafka message to normdaten topic!", e);
      importJob.setErrorMessage(e.getMessage());
      importJob.setResult(TotalResult.FAILED);
    }

  }
}

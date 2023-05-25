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

package staatsbibliothek.berlin.hsp.importservice.ui;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObject;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

@Slf4j
public abstract class ImportRestControllerTemplate {

  private static final String UNBEKANNTER_BEARBEITER = "unbekannterBearbeiter";

  protected ImportJobRepository importJobRepository;
  protected DateiImportBoundary dateiImportBoundary;
  protected DateiBoundary fileService;

  protected ImportRestControllerTemplate(
      ImportJobRepository importJobRepository,
      DateiImportBoundary dateiImportBoundary, DateiBoundary fileService) {

    this.importJobRepository = importJobRepository;
    this.dateiImportBoundary = dateiImportBoundary;
    this.fileService = fileService;
  }

  protected ResponseEntity<Void> doUpload(MultipartFile datei, ActivityStreamsDokumentTyp dokumentTyp,
      ActivityStreamObjectTag... activityStreamObjectTags) throws IOException {

    log.info("REST POST Request for import Datei Upload. ActivityStreamsDokumentTyp={}", dokumentTyp);

    if (fileService.isSupportedContent(datei)) {

      try {
        ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
            .withType(dokumentTyp)
            .withContent(datei.getBytes())
            .withTag(Arrays.asList(activityStreamObjectTags))
            .build();

        ImportJob importJob = dateiImportBoundary
            .importDateien(activityStreamObject, datei.getOriginalFilename(), UNBEKANNTER_BEARBEITER, dokumentTyp);

        Optional<ImportJob> job = importJobRepository.findById(importJob.getId());

        if (job.isPresent()) {
          return ResponseEntity.created(URI.create("/rest/import/job/" + job.get().getId())).build();
        } else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

      } catch (ActivityStreamsException e) {
        log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }

    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

  }
}

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

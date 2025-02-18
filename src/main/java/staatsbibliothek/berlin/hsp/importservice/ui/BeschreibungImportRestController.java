package staatsbibliothek.berlin.hsp.importservice.ui;

import static staatsbibliothek.berlin.hsp.importservice.ui.SwaggerConfig.TAG_BESCHREIBUNG;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamObjectTagId;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObjectTag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 07.07.21
 */

@RestController
@RequestMapping("/rest/beschreibung")
@Tag(name = TAG_BESCHREIBUNG)
public class BeschreibungImportRestController extends ImportRestControllerTemplate {

  private static final String INTERN = "intern";
  private static final String EXTERN = "extern";

  @Autowired
  public BeschreibungImportRestController(
      ImportJobRepository importJobRepository,
      @Qualifier("BeschreibungsImport") DateiImportBoundary dateiImportBoundary,
      DateiBoundary fileService) {

    super(importJobRepository, dateiImportBoundary, fileService);
  }

  @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import descriptions as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An import-job has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> upload(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.BESCHREIBUNG);
  }

  @PostMapping("/import/{verwaltungstyp}")
  @Operation(description = "Import descriptions as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An import-job has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> upload(
      @Parameter(description = "The administrative type of the uploaded descriptions, either INTERN or EXTERN", required = true)
      @PathVariable(name = "verwaltungstyp") String verwaltungstyp,

      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    ActivityStreamObjectTag internExternTag;
    if (INTERN.equalsIgnoreCase(verwaltungstyp)) {
      internExternTag = new HSPActivityStreamObjectTag("String",
          ActivityStreamObjectTagId.INTERN_EXTERN, INTERN);
    } else if (EXTERN.equalsIgnoreCase(verwaltungstyp)) {
      internExternTag = new HSPActivityStreamObjectTag("String",
          ActivityStreamObjectTagId.INTERN_EXTERN, EXTERN);
    } else {
      return ResponseEntity.notFound().build();
    }

    return doUpload(datei, ActivityStreamsDokumentTyp.BESCHREIBUNG, internExternTag);
  }

}

package staatsbibliothek.berlin.hsp.importservice.ui;


import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
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
 * @since 24.02.22
 */

@RestController
@RequestMapping("/rest/katalog")
@Tag(name = SwaggerConfig.TAG_KATALOG)
public class KatalogImportRestController extends ImportRestControllerTemplate {

  @Autowired
  public KatalogImportRestController(
      ImportJobRepository importJobRepository,
      @Qualifier("KatalogeImport") DateiImportBoundary dateiImportBoundary,
      DateiBoundary fileService) {

    super(importJobRepository, dateiImportBoundary, fileService);
  }

  @PostMapping(value = "/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import catalogs as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.")
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

    return doUpload(datei, ActivityStreamsDokumentTyp.KATALOG);
  }

}

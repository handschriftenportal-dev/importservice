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
 * @since 07.07.21
 */

@RestController
@RequestMapping("/rest/")
@Tag(name = SwaggerConfig.TAG_NORMDATEN)
public class NormdatenImportRestController extends ImportRestControllerTemplate {

  @Autowired
  public NormdatenImportRestController(
      ImportJobRepository importJobRepository,
      @Qualifier("NormdatenImport") DateiImportBoundary dateiImportBoundary,
      DateiBoundary fileService) {

    super(importJobRepository, dateiImportBoundary, fileService);
  }

  @PostMapping(value = "/ort/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import metadata-places as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      summary = "Place is modelled according to the GND Ontology class [Place or Geographic Name](https://d-nb.info/standards/elementset/gnd#PlaceOrGeographicName).")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadOrt(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.ORT);
  }

  @PostMapping(value = "/koerperschaft/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(
      description = "Import metadata-corporateBody as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      summary = "CorporateBody is modelled according to the GND Ontology class [Corporate Body](https://d-nb.info/standards/elementset/gnd#CorporateBody).")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadKoerperschaft(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.KOERPERSCHAFT);
  }

  @PostMapping(value = "/person/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import Normdaten-persons as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      summary = "Person is modelled according to the GND Ontology class [Person](https://d-nb.info/standards/elementset/gnd#Person).")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadPerson(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.PERSON);
  }

  @PostMapping(value = "/sprache/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import metadata-languages as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      summary = "Language is modelled according to the GND Ontology class [Language](https://d-nb.info/standards/elementset/gnd#Language).")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadSprache(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.SPRACHE);
  }

  @PostMapping(value = "/beziehung/import", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @Operation(description = "Import metadata-relations as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      summary = "Relations are modelled according to the RDF standard.")
  @ApiResponses({
      @ApiResponse(responseCode = "201",
          description = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded file could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadBeziehung(
      @Parameter(description = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.BEZIEHUNG);
  }

}

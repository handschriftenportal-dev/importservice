package staatsbibliothek.berlin.hsp.importservice.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import staatsbibliothek.berlin.hsp.importservice.domain.service.XMLService;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.ValidationResult;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 23.03.22
 */

@Slf4j
@RestController
@RequestMapping("/rest/tei-xml")
@Tag(name = SwaggerConfig.TAG_TEI_XML)
public class ValidationRestController {

  public static final String HEADER_PARAM_SCHEMA = "de.staatsbibliothek.berlin.hsp.schema";

  public static final String SCHEMA_ODD = "ODD";

  private XMLService xmlService;

  @Autowired
  public ValidationRestController(XMLService xmlService) {
    this.xmlService = xmlService;
  }

  @CrossOrigin(origins = {"${rest.teivalidate.cors.origin}"})
  @PostMapping(value = "/validate",
      consumes = {MediaType.APPLICATION_XML},
      produces = {MediaType.APPLICATION_JSON})
  @Operation(description = "Validate the uploaded TEI-XML-data.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "The uploaded data has been validated."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded data could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<ValidationResult> validateXmlTEI(
      @Parameter(description = "The TEI-XML-document to valid.", required = true)
      @RequestBody String xmlTEI,
      @Parameter(description =
          "Optional schema param. If set to ODD, the document is validated against TEI_ODD,"
              + " otherwise the document is validated against TEI_ALL.")
      @RequestHeader(value = HEADER_PARAM_SCHEMA, required = false) String schema,
      @Parameter(description = "Optional locale param for validation messages. Default: de")
      @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale) {

    log.info("POST Request for REST Interface tei-xml/validate.");

    if (locale == null || locale.isBlank()) {
      locale = "de";
    }

    ValidationResult validationResult = xmlService.validateXML(xmlTEI, SCHEMA_ODD.equals(schema),
        locale);

    return ResponseEntity.ok(validationResult);
  }
}

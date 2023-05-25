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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
@Api(tags = {SwaggerConfig.TAG_TEI_XML})
public class ValidationRestController {

  public static final String HEADER_PARAM_SCHEMA = "de.staatsbibliothek.berlin.hsp.schema";

  public static final String SCHEMA_ODD = "ODD";

  private XMLService xmlService;

  @Autowired
  public ValidationRestController(XMLService xmlService) {
    this.xmlService = xmlService;
  }

  @PostMapping(value = "/validate",
      consumes = {MediaType.APPLICATION_XML},
      produces = {MediaType.APPLICATION_JSON})
  @ApiOperation(value = "Validate the uploaded TEI-XML-data.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_OK,
          message = "The uploaded data has been validated."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded data could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<ValidationResult> validateXmlTEI(
      @ApiParam(value = "The TEI-XML-document to valid.", required = true)
      @RequestBody String xmlTEI,
      @ApiParam(value = "Optional schema param. If set to ODD, the document is validated against TEI_ODD,"
          + " otherwise the document is validated against TEI_ALL.")
      @RequestHeader(value = HEADER_PARAM_SCHEMA, required = false) String schema,
      @ApiParam(value = "Optional locale param for validation messages. Default: de")
      @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale) {

    log.info("POST Request for REST Interface tei-xml/validate.");

    if (locale == null || locale.isBlank()) {
      locale = "de";
    }

    ValidationResult validationResult = xmlService.validateXML(xmlTEI, SCHEMA_ODD.equals(schema), locale);

    return ResponseEntity.ok(validationResult);
  }
}

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
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import staatsbibliothek.berlin.hsp.importservice.domain.service.XMLService;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 23.03.22
 */

@Slf4j
@RestController
@RequestMapping("/rest/tei-xml")
@Api(tags = {SwaggerConfig.TAG_TEI_XML})
public class TransformationRestController {

  public static final String HSP2TEI = "HSP2TEI";
  public static final String TEI2HSP = "TEI2HSP";

  private XMLService xmlService;

  @Autowired
  public TransformationRestController(XMLService xmlService) {
    this.xmlService = xmlService;
  }

  @GetMapping("/tei2hsp")
  @ApiOperation(value = "Returns whether tei2hsp-transformation is enabled.")
  @ApiResponses({
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK,
          message = "The request was successfully handled."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Map<String, Boolean>> isTeiToHspEnabled() {

    log.info("GET Request for REST Interface tei-xml/tei2hsp");

    return ResponseEntity.ok(Collections.singletonMap("enabled", xmlService.isTeiToHspEnabled()));
  }

  @PostMapping(value = "/transform/{mode}",
      consumes = {MediaType.APPLICATION_XML},
      produces = {MediaType.APPLICATION_XML})
  @ApiOperation(value = "Transform the uploaded TEI-XML-data from tei to hsp or vice versa.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_OK,
          message = "The uploaded data has been transformed."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded data could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<String> transform(
      @ApiParam(value = "The transformation-mode: TEI2HSP or HSP2TEI ", required = true)
      @PathVariable(name = "mode") String mode,
      @ApiParam(value = "The TEI-XML-document to transform.", required = true)
      @RequestBody String xmlData) {

    log.info("POST Request for REST Interface tei-xml/transform, mode={}", mode);

    if (TEI2HSP.equals(mode)) {
      try {
        return ResponseEntity.ok(xmlService.transformTeiToHsp(xmlData));
      } catch (Exception e) {
        log.error("Error in transformTeiToHsp!", e);
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(e.getMessage());
      }
    } else if (HSP2TEI.equals(mode)) {
      try {
        return ResponseEntity.ok(xmlService.transformHspToTei(xmlData));
      } catch (Exception e) {
        log.error("Error in transformHspToTei!", e);
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body(e.getMessage());
      }
    } else {
      log.info("bad request, mode not supported: " + mode);
      return ResponseEntity.badRequest().body("mode: " + mode);
    }
  }
}

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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamObjectTagId;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObjectTag;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Api(tags = {SwaggerConfig.TAG_BESCHREIBUNG})
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

  @PostMapping("/import")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import descriptions as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An import-job has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> upload(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.BESCHREIBUNG);
  }

  @PostMapping("/import/{verwaltungstyp}")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import descriptions as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An import-job has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> upload(
      @ApiParam(value = "The administrative type of the uploaded descriptions, either INTERN or EXTERN", required = true)
      @PathVariable(name = "verwaltungstyp") String verwaltungstyp,

      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    ActivityStreamObjectTag internExternTag;
    if (INTERN.equalsIgnoreCase(verwaltungstyp)) {
      internExternTag = new HSPActivityStreamObjectTag("String", ActivityStreamObjectTagId.INTERN_EXTERN, INTERN);
    } else if (EXTERN.equalsIgnoreCase(verwaltungstyp)) {
      internExternTag = new HSPActivityStreamObjectTag("String", ActivityStreamObjectTagId.INTERN_EXTERN, EXTERN);
    } else {
      return ResponseEntity.notFound().build();
    }

    return doUpload(datei, ActivityStreamsDokumentTyp.BESCHREIBUNG, internExternTag);
  }

}

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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
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
@Api(tags = {SwaggerConfig.TAG_NORMDATEN})
public class NormdatenImportRestController extends ImportRestControllerTemplate {

  @Autowired
  public NormdatenImportRestController(
      ImportJobRepository importJobRepository,
      @Qualifier("NormdatenImport") DateiImportBoundary dateiImportBoundary,
      DateiBoundary fileService) {

    super(importJobRepository, dateiImportBoundary, fileService);
  }

  @PostMapping("/ort/import")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import metadata-places as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      notes = "Place is modelled according to the GND Ontology class [Place or Geographic Name](https://d-nb.info/standards/elementset/gnd#PlaceOrGeographicName).")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadOrt(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.ORT);
  }

  @PostMapping("/koerperschaft/import")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import metadata-corporateBody as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      notes = "CorporateBody is modelled according to the GND Ontology class [Corporate Body](https://d-nb.info/standards/elementset/gnd#CorporateBody).")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadKoerperschaft(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.KOERPERSCHAFT);
  }

  @PostMapping("/person/import")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import Normdaten-persons as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      notes = "Person is modelled according to the GND Ontology class [Person](https://d-nb.info/standards/elementset/gnd#Person).")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadPerson(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.PERSON);
  }

  @PostMapping("/sprache/import")
  @ApiOperation(code = HttpStatus.SC_CREATED,
      value = "Import metadata-languages as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      notes = "Language is modelled according to the GND Ontology class [Language](https://d-nb.info/standards/elementset/gnd#Language).")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadSprache(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.SPRACHE);
  }

  @PostMapping("/beziehung/import")
  @ApiOperation(code = HttpStatus.SC_CREATED, response = Void.class,
      value = "Import metadata-relations as a single TEI-XML-document or multiple TEI-XML-documents as zip-file.",
      notes = "Relations are modelled according to the RDF standard.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_CREATED,
          message = "An ImportJob has successfully been created from the uploaded file."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded file could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<Void> uploadBeziehung(
      @ApiParam(value = "A single TEI-XML-document or multiple TEI-XML-documents as zip-file.", required = true)
      @RequestParam("datei") MultipartFile datei)
      throws IOException {

    return doUpload(datei, ActivityStreamsDokumentTyp.BEZIEHUNG);
  }

}

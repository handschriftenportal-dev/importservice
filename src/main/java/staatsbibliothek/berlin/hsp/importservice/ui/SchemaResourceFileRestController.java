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

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.util.Optional;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 23.03.22
 */

@Slf4j
@RestController
@RequestMapping("/rest")
@Api(tags = {SwaggerConfig.TAG_SCHEMARESOURCEFILE})
public class SchemaResourceFileRestController {

  private final SchemaResourceFileBoundary boundary;

  @Autowired
  public SchemaResourceFileRestController(SchemaResourceFileBoundary boundary) {
    this.boundary = boundary;
  }

  @GetMapping("/schemaresourcefile")
  @JsonView(Views.List.class)
  @ApiOperation(value = "Returns all schema-resource-files.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_OK,
          message = "All existing schema-resource-files have been returned."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public Iterable<SchemaResourceFile> findAll() throws IOException {

    log.info("GET Request for REST Interface schemaresourcefile.");

    return boundary.findAll();
  }

  @GetMapping("/schemaresourcefile/{id}")
  @ApiOperation(value = "Returns the schema-resource-file for the given identifier.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_OK,
          message = "The requested schema-resource-file has been returned."),
      @ApiResponse(code = HttpStatus.SC_NOT_FOUND,
          message = "No schema-resource-file has been found for the given identifier."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<SchemaResourceFile> findById(
      @ApiParam(value = "The identifier of the requested schema-resource-file.", required = true)
      @PathVariable(name = "id") String id)
      throws IOException {

    log.info("GET Request for REST Interface schemaresourcefile id={}.", id);

    Optional<SchemaResourceFile> schemaResourceFile = boundary.findOptionalById(id);
    return schemaResourceFile.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).build());
  }

  @PutMapping("/schemaresourcefile/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Update a schema-resource-file with the uploaded data.")
  @ApiResponses({
      @ApiResponse(code = HttpStatus.SC_OK,
          message = "The schema-resource-file has been updated with the uploaded data."),
      @ApiResponse(code = HttpStatus.SC_BAD_REQUEST,
          message = "The uploaded data could not be processed."),
      @ApiResponse(code = HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<String> upload(
      @ApiParam(value = "The id of the schema-resource-file.", required = true)
      @PathVariable(name = "id") String id,

      @ApiParam(value = "The version of the schema-resource-file.", required = true)
      @RequestPart(value = "version") String version,

      //TODO get bearbeiterName from authentication
      @ApiParam(value = "The bearbeiterName of the uploading person.", required = true)
      @RequestPart("bearbeiterName") String bearbeiterName,

      @ApiParam(value = "The schema-resource-file.", required = true)
      @RequestPart("datei") MultipartFile datei)
      throws IOException {

    log.info("PUT Request for REST Interface schemaresourcefile id={}, version={}, bearbeiterName={}",
        id, version, bearbeiterName);

    try {
      boundary.save(id, datei.getBytes(), bearbeiterName, Version.parse(version));
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error saving id " + id, e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}



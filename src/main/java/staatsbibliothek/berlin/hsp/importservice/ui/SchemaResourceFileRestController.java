package staatsbibliothek.berlin.hsp.importservice.ui;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = SwaggerConfig.TAG_SCHEMARESOURCEFILE)
public class SchemaResourceFileRestController {

  private final SchemaResourceFileBoundary boundary;

  @Autowired
  public SchemaResourceFileRestController(SchemaResourceFileBoundary boundary) {
    this.boundary = boundary;
  }

  @GetMapping("/schemaresourcefile")
  @JsonView(Views.List.class)
  @Operation(description = "Returns all schema-resource-files.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "All existing schema-resource-files have been returned."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public Iterable<SchemaResourceFile> findAll() throws IOException {

    log.info("GET Request for REST Interface schemaresourcefile.");

    return boundary.findAll();
  }

  @GetMapping("/schemaresourcefile/{idOrFilename}")
  @Operation(description = "Returns the schema-resource-file for the given identifier.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "The requested schema-resource-file has been returned."),
      @ApiResponse(responseCode = "404",
          description = "No schema-resource-file has been found for the given identifier."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<SchemaResourceFile> findByIdOrFilename(
      @Parameter(description = "The identifier of the requested schema-resource-file.", required = true)
      @PathVariable(name = "idOrFilename") String idOrFilename)
      throws IOException {

    log.info("GET Request for REST Interface schemaresourcefile id={}.", idOrFilename);

    String uuid = getUUIDorFilename(idOrFilename);

    Optional<SchemaResourceFile> schemaResourceFile = boundary.findOptionalById(uuid);
    return schemaResourceFile.map(ResponseEntity::ok)
        .orElseGet(
            () -> ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND).build());
  }

  @PutMapping("/schemaresourcefile/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Operation(description = "Update a schema-resource-file with the uploaded data.")
  @ApiResponses({
      @ApiResponse(responseCode = "200",
          description = "The schema-resource-file has been updated with the uploaded data."),
      @ApiResponse(responseCode = "400",
          description = "The uploaded data could not be processed."),
      @ApiResponse(responseCode = "500",
          description = "An internal error happened during handling the request."),
  })
  public ResponseEntity<String> upload(
      @Parameter(description = "The id of the schema-resource-file.", required = true)
      @PathVariable(name = "id") String id,

      @Parameter(description = "The version of the schema-resource-file.", required = true)
      @RequestPart(value = "version") String version,

      //TODO get bearbeiterName from authentication
      @Parameter(description = "The bearbeiterName of the uploading person.", required = true)
      @RequestPart("bearbeiterName") String bearbeiterName,

      @Parameter(description = "The schema-resource-file.", required = true)
      @RequestPart("datei") MultipartFile datei)
      throws IOException {

    log.info(
        "PUT Request for REST Interface schemaresourcefile id={}, version={}, bearbeiterName={}",
        id, version, bearbeiterName);

    try {
      boundary.save(id, datei.getBytes(), bearbeiterName, Version.parse(version));
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error saving id {}", id, e);
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Returns the UUID if the given string is a valid UUID, otherwise returns the UUID of the given
   * string.
   *
   * @param id the string to check
   * @return the UUID or the UUID of the given string
   */
  String getUUIDorFilename(String id) {
    try {
      UUID.fromString(id);
      return id;
    } catch (IllegalArgumentException e) {
      return UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)).toString();
    }
  }
}



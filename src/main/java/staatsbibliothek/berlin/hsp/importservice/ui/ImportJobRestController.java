package staatsbibliothek.berlin.hsp.importservice.ui;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */

@RestController
@RequestMapping("/rest")
@Slf4j
@Tag(name = SwaggerConfig.TAG_IMPORTJOB)
public class ImportJobRestController {

  private final ImportJobRepository importJobRepository;

  @Autowired
  public ImportJobRestController(ImportJobRepository importJobRepository) {
    this.importJobRepository = importJobRepository;
  }

  @GetMapping("/import/job")
  @JsonView(Views.List.class)
  @Operation(description = "Returns all import-jobs including their current state of processing.")
  @ApiResponse(responseCode = "200",
      description = "All existing import-jobs have been returned.")
  @ApiResponse(responseCode = "500",
      description = "An internal error happened during handling the request.")
  public Iterable<ImportJob> findAllImportJobs() {

    log.info("GET Request for REST Interface ImportJobs");

    return importJobRepository.findAll();
  }

  @DeleteMapping("/import/job")
  @Operation(description = "Delete ImportJobs by given identifiers.")
  @ApiResponse(responseCode = "200",
      description = "The import-job has been successfully deleted .")
  @ApiResponse(responseCode = "400",
      description = "Invalid request.")
  public ResponseEntity<String> deleteAllImportJobsById(@RequestBody List<String> ids) {

    log.info("Delete all import-jobs");

    try {
      importJobRepository.deleteAllById(ids);
      String message = "Die Import-Jobs mit den IDs " + ids + " wurden gelöscht";
      return new ResponseEntity<>(message, HttpStatus.OK);
    } catch (EmptyResultDataAccessException exception) {
      return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/import/job/{id}")
  @Operation(description = "Returns the import-job for the given identifier.")

  @ApiResponse(responseCode = "200",
      description = "The requested import-job has been returned.")
  @ApiResponse(responseCode = "404",
      description = "No import-job has been found for the given identifier.")
  @ApiResponse(responseCode = "500",
      description = "An internal error happened during handling the request.")
  public ResponseEntity<ImportJob> findImportJob(
      @Parameter(description = "The identifier of the requested import-job.", required = true)
      @PathVariable(name = "id") String jobid) {

    log.info("GET Request for REST Interface ImportJob {}", jobid);

    Optional<ImportJob> job = importJobRepository.findById(jobid);

    return job.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
  }
}

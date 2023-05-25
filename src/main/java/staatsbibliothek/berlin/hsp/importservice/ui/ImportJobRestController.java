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
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@Api(tags = {SwaggerConfig.TAG_IMPORTJOB})
public class ImportJobRestController {

  private ImportJobRepository importJobRepository;

  @Autowired
  public ImportJobRestController(ImportJobRepository importJobRepository) {
    this.importJobRepository = importJobRepository;
  }

  @GetMapping("/import/job")
  @JsonView(Views.List.class)
  @ApiOperation(code = org.apache.http.HttpStatus.SC_OK,
      value = "Returns all import-jobs including their current state of processing.")
  @ApiResponses({
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK,
          message = "All existing import-jobs have been returned."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public Iterable<ImportJob> findAllImportJobs() {

    log.info("GET Request for REST Interface ImportJobs");

    return importJobRepository.findAll();
  }

  @GetMapping("/import/job/{id}")
  @ApiOperation(code = org.apache.http.HttpStatus.SC_OK,
      value = "Returns the import-job for the given identifier.")
  @ApiResponses({
      @ApiResponse(code = org.apache.http.HttpStatus.SC_OK,
          message = "The requested import-job has been returned."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_NOT_FOUND,
          message = "No import-job has been found for the given identifier."),
      @ApiResponse(code = org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR,
          message = "An internal error happened during handling the request."),
  })
  public ResponseEntity<ImportJob> findImportJob(
      @ApiParam(value = "The identifier of the requested import-job.", required = true)
      @PathVariable(name = "id") String jobid) {

    log.info("GET Request for REST Interface ImportJob {}", jobid);

    Optional<ImportJob> job = importJobRepository.findById(jobid);

    if (job.isPresent()) {

      return ResponseEntity.ok(job.get());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

}

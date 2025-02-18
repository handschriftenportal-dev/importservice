package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */
@SpringBootTest()
@AutoConfigureMockMvc
public class ImportJobRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImportJobRepository importJobRepository;

  @Test
  void testFindAllImportJobs() throws Exception {
    ImportJob job = new ImportJob(null, "testFile.xml", "Konrad Eichstädt");

    importJobRepository.deleteAll();

    importJobRepository.save(job);

    this.mockMvc.perform(get("/rest/import/job").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(job.getId())))
        .andExpect(jsonPath("$[0].benutzerName", is(job.getBenutzerName())))
        .andExpect(jsonPath("$[0].name", is(job.getName())));
  }

  @Test
  void testfindImportJob() throws Exception {
    ImportJob job = new ImportJob(null, "testFile.xml", "Konrad Eichstädt");

    importJobRepository.save(job);

    this.mockMvc.perform(
            get("/rest/import/job/" + job.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(job.getId())))
        .andExpect(jsonPath("$.benutzerName", is(job.getBenutzerName())))
        .andExpect(jsonPath("$.name", is(job.getName())));
  }

  @Test
  void deleteImportJobByIdWhenJobExistsShouldReturnOk() throws Exception {

    ObjectMapper objectMapper = new ObjectMapper();

    String jobId = "test";
    ImportJob importJob = new ImportJob(jobId, "testFile.xml", "null", "");
    importJobRepository.save(importJob);
    mockMvc.perform(delete("/rest/import/job").contentType("application/json")
            .content(objectMapper.writeValueAsString(List.of(importJob.getId()))))
        .andExpect(status().isOk())
        .andExpect(content().string("Die Import-Jobs mit den IDs [test] wurden gelöscht"));

    assertFalse(importJobRepository.existsById(jobId));
  }

}

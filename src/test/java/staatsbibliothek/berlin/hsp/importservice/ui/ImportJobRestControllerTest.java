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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    this.mockMvc.perform(get("/rest/import/job/")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(
            jsonPath("$[0].id", is(job.getId())))
        .andExpect(jsonPath("$[0].benutzerName",
            is(job.getBenutzerName())))
        .andExpect(jsonPath("$[0].name",
            is(job.getName())));
  }

  @Test
  void testfindImportJob() throws Exception {
    ImportJob job = new ImportJob(null, "testFile.xml", "Konrad Eichstädt");

    importJobRepository.save(job);

    this.mockMvc.perform(get("/rest/import/job/" + job.getId())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(job.getId())))
        .andExpect(jsonPath("$.benutzerName", is(job.getBenutzerName())))
        .andExpect(jsonPath("$.name", is(job.getName())));
  }

}

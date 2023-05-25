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

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.service.BeschreibungenImportService;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@SpringBootTest()
@AutoConfigureMockMvc
public class ImportViewControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImportJobRepository importJobRepository;

  @MockBean
  private BeschreibungenImportService beschreibungenImportService;

  @Test
  public void testGetImportPage() throws Exception {
    this.mockMvc.perform(get("/import"))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void testUploadFile() throws Exception {
    ImportJob job = new ImportJob(null, "test.xml", "Konrad Eichstädt");
    Mockito.when(beschreibungenImportService.importDateien(any(), any(), any(), any())).thenReturn(job);

    MockMultipartFile multipartFile = new MockMultipartFile("datei", "test.xml",
        "application/xml", "Spring Framework".getBytes());
    this.mockMvc
        .perform(multipart("/import").file(multipartFile).header("referer", "http://localhost:9298/import.xhtml"))
        .andExpect(status().isFound());
  }

  @Test
  void testgetImportJobPage() throws Exception {
    ImportJob job = new ImportJob(null, "testFile.xml", "Konrad Eichstädt");

    importJobRepository.save(job);

    this.mockMvc.perform(get("/import/job/" + job.getId()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().string(containsString(
            "<h3>Handschriftenportal data import</h3>")));
  }

}

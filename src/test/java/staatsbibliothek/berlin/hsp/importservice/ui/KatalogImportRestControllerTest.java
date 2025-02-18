package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.service.KatalogeImportService;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 24.02.2022
 */

@SpringBootTest()
@AutoConfigureMockMvc
public class KatalogImportRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImportJobRepository importJobRepository;

  @MockBean
  private KatalogeImportService katalogeImportServiceImportService;

  @Test
  void testUpload() throws Exception {
    ImportJob job = new ImportJob(null, "test.xml", "Konrad Eichstädt");
    Mockito.when(katalogeImportServiceImportService.importDateien(any(), any(), any(), any())).thenReturn(job);

    importJobRepository.save(job);

    MockMultipartFile multipartFile = new MockMultipartFile("datei", "test.xml",
        "application/xml", "Spring Framework".getBytes());
    this.mockMvc.perform(multipart("/rest/katalog/import").file(multipartFile))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

}

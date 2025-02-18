package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.service.NormdatenImportService;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 07.07.21
 */

@SpringBootTest()
@AutoConfigureMockMvc
public class NormdatenRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ImportJobRepository importJobRepository;

  @MockBean
  private NormdatenImportService normdatenImportService;

  @ParameterizedTest
  @ValueSource(strings = {"ort", "koerperschaft", "person", "sprache", "beziehung"})
  void testUpload(String normdatum) throws Exception {
    ImportJob job = new ImportJob(null, "test.xml", "Konrad Eichstädt");
    importJobRepository.save(job);

    Mockito.when(normdatenImportService.importDateien(any(), any(), any(), any())).thenReturn(job);

    MockMultipartFile multipartFile = new MockMultipartFile("datei", "test.xml",
        "application/xml", "Spring Framework".getBytes());

    this.mockMvc.perform(multipart("/rest/" + normdatum + "/import").file(multipartFile))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

}

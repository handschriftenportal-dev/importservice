package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import jakarta.ws.rs.core.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 23.03.22
 */

@SpringBootTest()
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class ValidationRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testValidateValid() throws Exception {
    String xmlTeiData = Files.readString(
        Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_hsp.xml"));

    this.mockMvc.perform(post("/rest/tei-xml/validate")
            .contentType(MediaType.APPLICATION_XML)
            .content(xmlTeiData)
            .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid", is(true)))
        .andExpect(jsonPath("$.message", is("Document is valid")));
  }

  @Test
  void testValidateInvalid() throws Exception {
    String xmlTeiData = Files.readString(
        Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_invalid.xml"));

    this.mockMvc.perform(post("/rest/tei-xml/validate")
            .contentType(MediaType.APPLICATION_XML)
            .content(xmlTeiData)
            .header(ValidationRestController.HEADER_PARAM_SCHEMA, ValidationRestController.SCHEMA_ODD)
            .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid", is(false)))
        .andExpect(jsonPath("$.line", is("24")))
        .andExpect(jsonPath("$.column", is("19")))
        .andExpect(jsonPath("$.message", is("Document not is valid -> "
            + "element \"head\" not allowed yet; missing required element \"msIdentifier\"")));
  }
}


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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.core.HttpHeaders;
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
    String xmlTeiData = Files.readString(Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_hsp.xml"));

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
    String xmlTeiData = Files.readString(Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_invalid.xml"));

    this.mockMvc.perform(post("/rest/tei-xml/validate")
            .contentType(MediaType.APPLICATION_XML)
            .content(xmlTeiData)
            .header(ValidationRestController.HEADER_PARAM_SCHEMA, ValidationRestController.SCHEMA_ODD)
            .header(HttpHeaders.ACCEPT_LANGUAGE, "en"))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.valid", is(false)))
        .andExpect(jsonPath("$.line", is("17")))
        .andExpect(jsonPath("$.column", is("24")))
        .andExpect(jsonPath("$.message", is("Document not is valid -> "
            + "element \"head\" not allowed yet; missing required element \"msIdentifier\"")));
  }
}


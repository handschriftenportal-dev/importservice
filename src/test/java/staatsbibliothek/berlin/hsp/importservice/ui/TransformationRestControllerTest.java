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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 23.03.22
 */

@SpringBootTest()
@TestPropertySource(locations = "classpath:test.properties")
@AutoConfigureMockMvc
public class TransformationRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testTransformBadRequest() throws Exception {
    this.mockMvc.perform(post("/rest/tei-xml/transform/badMode")
            .contentType(MediaType.APPLICATION_XML)
            .content("any"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testTransformTeiToHsp() throws Exception {
    String requestBody = Files.readString(Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare.xml"));
    String responseBody = Files.readString(Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_hsp.xml"));

    this.mockMvc.perform(post("/rest/tei-xml/transform/TEI2HSP")
            .contentType(MediaType.APPLICATION_XML)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string(responseBody));
  }

  @Test
  void testTransformHspToTei() throws Exception {
    String requestBody = Files.readString(Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_hsp.xml"));
    String responseBody = Files.readString(
        Paths.get("src", "test", "resources", "tei-msDesc_hsp_bare_hsp_reverse.xml"));

    this.mockMvc.perform(post("/rest/tei-xml/transform/HSP2TEI")
            .contentType(MediaType.APPLICATION_XML)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string(responseBody));
  }

  @Test
  void testIsTeiToHspEnabled() throws Exception {
    this.mockMvc.perform(get("/rest/tei-xml/tei2hsp"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.enabled", is(true)));
  }

}

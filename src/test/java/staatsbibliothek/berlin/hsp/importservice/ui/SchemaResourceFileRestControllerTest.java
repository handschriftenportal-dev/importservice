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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.service.SchemaResourceFileService;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 22.03.22
 */

@SpringBootTest
@ResourceLock("schemaresourcesdirectory")
@Execution(SAME_THREAD)
@AutoConfigureMockMvc
public class SchemaResourceFileRestControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private SchemaResourceFileBoundary schemaResourceFileService;

  @Test
  void testFindAll() throws Exception {
    this.mockMvc.perform(get("/rest/schemaresourcefile")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(14)));
  }

  @Test
  void testFindById() throws Exception {
    this.mockMvc.perform(get("/rest/schemaresourcefile/" + SchemaResourceFileBoundary.ID_XSLT_MXML_TO_TEI)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(SchemaResourceFileBoundary.ID_XSLT_MXML_TO_TEI)))
        .andExpect(jsonPath("$.xmlFormat", is("MXML")))
        .andExpect(jsonPath("$.schemaResourceTyp", is("XSLT")))
        .andExpect(jsonPath("$.dateiName", is("MXML-to-TEI-P5.xsl")))
        .andExpect(jsonPath("$.bearbeitername", is("system")))
        .andExpect(jsonPath("$.datei", containsString("xml version=\"1.0\"")));
  }

  @Test
  @Transactional
  void testUpload() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("datei", "test.xml",
        "application/xml", "test".getBytes());

    String bearbeitername = "tester";
    String version = "1.0.99";

    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders
        .multipart("/rest/schemaresourcefile/" + SchemaResourceFileBoundary.ID_XSLT_MXML_TO_TEI);
    builder.with(request -> {
      request.setMethod("PUT");
      return request;
    });

    MockPart bearbeiterPart = new MockPart("bearbeiterName", bearbeitername.getBytes(StandardCharsets.UTF_8));
    bearbeiterPart.getHeaders().setContentType(MediaType.TEXT_PLAIN);

    MockPart versionPart = new MockPart("version", version.getBytes(StandardCharsets.UTF_8));
    versionPart.getHeaders().setContentType(MediaType.TEXT_PLAIN);

    this.mockMvc.perform(builder.file(multipartFile)
            .part(bearbeiterPart, versionPart))
        .andDo(MockMvcResultHandlers.log())
        .andExpect(status().isOk());

    Optional<SchemaResourceFile> result = schemaResourceFileService.findOptionalById(
        SchemaResourceFileService.ID_XSLT_MXML_TO_TEI);

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(SchemaResourceFileService.ID_XSLT_MXML_TO_TEI, result.get().getId());
    assertEquals(bearbeitername, result.get().getBearbeitername());
    assertNotNull(result.get().getVersion());
    assertEquals(version, result.get().getVersion().toString());
    assertNotNull(result.get().getDatei());
    assertTrue(result.get().getDatei().isPresent());
    assertTrue(result.get().getDatei().get().getURL().toString().endsWith("MXML-to-TEI-P5.xsl"));
  }

}
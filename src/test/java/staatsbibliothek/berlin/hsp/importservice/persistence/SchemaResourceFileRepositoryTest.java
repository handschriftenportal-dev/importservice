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

package staatsbibliothek.berlin.hsp.importservice.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 22.03.22
 */

@SpringBootTest
public class SchemaResourceFileRepositoryTest {

  @Autowired
  private SchemaResourceFileRepository schemaResourceFileRepository;

  @Test
  void testFindAll() {
    Iterable<SchemaResourceFile> result = schemaResourceFileRepository.findAll();

    assertNotNull(result);
    assertEquals(14, StreamSupport.stream(result.spliterator(), false).count());
  }

  @Test
  void testFindById() {
    Optional<SchemaResourceFile> result = schemaResourceFileRepository.findById("90d6c42a-6cf8-3fd6-b28b-de8493f20734");

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals("90d6c42a-6cf8-3fd6-b28b-de8493f20734", result.get().getId());
    assertEquals("MXML-to-TEI-P5.xsl", result.get().getDateiName());
    assertEquals(SchemaResourceTyp.XSLT, result.get().getSchemaResourceTyp());
    assertEquals(XMLFormate.MXML, result.get().getXmlFormat());
    assertEquals("system", result.get().getBearbeitername());
    assertNotNull(result.get().getDatei());
    assertFalse(result.get().getDatei().isPresent());
    assertNotNull(result.get().getErstellungsDatum());
    assertNotNull(result.get().getAenderungsDatum());
  }
}

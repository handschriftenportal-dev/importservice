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

package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */
public class ImportFileTest {

  @Test
  void testCreation() {
    ImportFile result = new ImportFile(UUID.randomUUID().toString(), "text.xml", new File("text.xml").toPath(),
        "application/xml", XMLFormate.MARC21, true, "Success");

    Assertions.assertEquals("text.xml", result.getPath().toFile().getName());
    Assertions.assertEquals("application/xml", result.getDateiTyp());
    Assertions.assertEquals(XMLFormate.MARC21, result.getDateiFormat());
    Assertions.assertEquals(true, result.isError());
  }

  @Test
  void testEquals() {
    ImportFile result = new ImportFile(UUID.randomUUID().toString(), "text.xml", new File("text.xml").toPath(),
        "application/xml",
        XMLFormate.MARC21, true,
        "Success");

    ImportFile result1 = new ImportFile(UUID.randomUUID().toString(), "text.xml", new File("text.xml").toPath(),
        "application/xml",
        XMLFormate.MARC21, true,
        "Success");

    ImportFile result3 = new ImportFile(UUID.randomUUID().toString(), "text.xml", new File("Bonn.xml").toPath(),
        "application/xml",
        XMLFormate.MARC21, true,
        "Success");

    Assertions.assertNotEquals(result, result1);

    Assertions.assertNotEquals(result, result3);
  }

}

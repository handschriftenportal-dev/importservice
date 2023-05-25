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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 02.09.22
 */

public class SchemaResourceFileTest {

  @Test
  void testCreation() {

    String id = "id";
    XMLFormate xmlFormat = XMLFormate.MXML;
    SchemaResourceTyp schemaResourceTyp = SchemaResourceTyp.XSLT;
    String dateiName = "datei.name";
    String bearbeitername = "system";
    Version version = Version.parse("1.2.3");
    LocalDateTime erstellungsDatum = LocalDateTime.now();
    LocalDateTime aenderungsDatum = LocalDateTime.now();

    SchemaResourceFile schemaResourceFile = SchemaResourceFile.builder()
        .withId(id)
        .withXmlFormat(xmlFormat)
        .withSchemaResourceTyp(schemaResourceTyp)
        .withDateiName(dateiName)
        .withBearbeitername(bearbeitername)
        .withVersion(Version.parse("1.2.3"))
        .withErstellungsDatum(erstellungsDatum)
        .withAenderungsDatum(aenderungsDatum)
        .withDatei(Optional.empty())
        .build();

    assertNotNull(schemaResourceFile);
    assertEquals(id, schemaResourceFile.getId());
    assertEquals(xmlFormat, schemaResourceFile.getXmlFormat());
    assertEquals(schemaResourceTyp, schemaResourceFile.getSchemaResourceTyp());
    assertEquals(dateiName, schemaResourceFile.getDateiName());
    assertEquals(bearbeitername, schemaResourceFile.getBearbeitername());
    assertEquals(version, schemaResourceFile.getVersion());
    assertEquals(erstellungsDatum, schemaResourceFile.getErstellungsDatum());
    assertEquals(aenderungsDatum, schemaResourceFile.getAenderungsDatum());
    assertNotNull(schemaResourceFile.getDatei());
    assertFalse(schemaResourceFile.getDatei().isPresent());
  }
}

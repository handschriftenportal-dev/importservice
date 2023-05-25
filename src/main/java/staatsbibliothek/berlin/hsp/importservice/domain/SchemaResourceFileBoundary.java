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

package staatsbibliothek.berlin.hsp.importservice.domain;

import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 21.03.22
 */

public interface SchemaResourceFileBoundary {

  String ID_XSLT_MXML_TO_TEI =
      UUID.nameUUIDFromBytes("MXML-to-TEI-P5.xsl".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSLT_TEI_TO_TEI =
      UUID.nameUUIDFromBytes("TEI-to-TEI-HSP.xsl".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSLT_HSP_TO_TEI =
      UUID.nameUUIDFromBytes("switch_HSP-to-TEI.xsl".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSLT_TEI_TO_HSP =
      UUID.nameUUIDFromBytes("switch_TEI-to-HSP.xsl".getBytes(StandardCharsets.UTF_8)).toString();

  String ID_ISOSCH_TEI_ALL =
      UUID.nameUUIDFromBytes("tei_all.isosch".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_ISOSCH_TEI_HSP =
      UUID.nameUUIDFromBytes("hsp_cataloguing.isosch".getBytes(StandardCharsets.UTF_8)).toString();

  String ID_RNG_TEI_ALL =
      UUID.nameUUIDFromBytes("tei_all.rng".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_RNG_TEI_HSP =
      UUID.nameUUIDFromBytes("hsp_cataloguing.rng".getBytes(StandardCharsets.UTF_8)).toString();

  String ID_XSD_MARC21 =
      UUID.nameUUIDFromBytes("MARC21slim.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_MXML =
      UUID.nameUUIDFromBytes("hida-schema.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL =
      UUID.nameUUIDFromBytes("tei_all.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL_DCR =
      UUID.nameUUIDFromBytes("tei_all_dcr.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL_TEIX =
      UUID.nameUUIDFromBytes("tei_all_teix.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL_XML =
      UUID.nameUUIDFromBytes("tei_all_xml.xsd".getBytes(StandardCharsets.UTF_8)).toString();

  String[] SCHEMA_RESOURCE_IDS = {
      ID_XSLT_MXML_TO_TEI, ID_XSLT_TEI_TO_TEI, ID_XSLT_HSP_TO_TEI, ID_XSLT_TEI_TO_HSP, ID_ISOSCH_TEI_ALL,
      ID_ISOSCH_TEI_HSP, ID_RNG_TEI_ALL, ID_RNG_TEI_HSP, ID_XSD_MARC21, ID_XSD_MXML, ID_XSD_TEI_ALL,
      ID_XSD_TEI_ALL_DCR, ID_XSD_TEI_ALL_TEIX, ID_XSD_TEI_ALL_XML};

  Iterable<SchemaResourceFile> findAll();

  Optional<SchemaResourceFile> findOptionalById(String id) throws IOException;

  void save(String id, byte[] datei, String bearbeitername, Version version) throws Exception;

}

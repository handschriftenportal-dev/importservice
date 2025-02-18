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

  String ID_XSLT_TEI_TO_DC =
      UUID.nameUUIDFromBytes("tei2dc.xsl".getBytes(StandardCharsets.UTF_8)).toString();

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
      UUID.nameUUIDFromBytes("dcr.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL_TEIX =
      UUID.nameUUIDFromBytes("teix.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_TEI_ALL_XML =
      UUID.nameUUIDFromBytes("xml.xsd".getBytes(StandardCharsets.UTF_8)).toString();
  String ID_XSD_HSP_CATALOGUING_XML =
      UUID.nameUUIDFromBytes("hsp_cataloguing.xsd".getBytes(StandardCharsets.UTF_8)).toString();

  String ID_XSD_TEI_ALL_XINCLUDE =
      UUID.nameUUIDFromBytes("xinclude.xsd".getBytes(StandardCharsets.UTF_8)).toString();

  String ID_RDF_TURTLE =
      UUID.nameUUIDFromBytes("hspo_skos.shacl.ttl".getBytes(StandardCharsets.UTF_8)).toString();


  String[] SCHEMA_RESOURCE_IDS = {
      ID_XSLT_MXML_TO_TEI, ID_XSLT_TEI_TO_TEI, ID_XSLT_TEI_TO_DC,
      ID_ISOSCH_TEI_ALL,
      ID_ISOSCH_TEI_HSP, ID_RNG_TEI_ALL, ID_RNG_TEI_HSP, ID_XSD_MARC21, ID_XSD_MXML, ID_XSD_TEI_ALL,
      ID_XSD_TEI_ALL_DCR, ID_XSD_TEI_ALL_TEIX, ID_XSD_TEI_ALL_XML, ID_XSD_TEI_ALL_XINCLUDE,
      ID_RDF_TURTLE, ID_XSD_HSP_CATALOGUING_XML};

  Iterable<SchemaResourceFile> findAll();

  Optional<SchemaResourceFile> findOptionalById(String id) throws IOException;

  void save(String id, byte[] datei, String bearbeitername, Version version) throws Exception;

}

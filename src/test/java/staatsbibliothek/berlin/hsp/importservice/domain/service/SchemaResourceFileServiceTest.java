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

package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_XSD_MARC21;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_XSLT_MXML_TO_TEI;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.SCHEMA_RESOURCE_IDS;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 21.03.22
 */

@SpringBootTest
@ResourceLock("schemaresourcesdirectory")
@Execution(SAME_THREAD)
@Rollback
public class SchemaResourceFileServiceTest {

  @Autowired
  private SchemaResourceFileBoundary schemaResourceFileService;

  @Test
  void testIds() {
    assertEquals("90d6c42a-6cf8-3fd6-b28b-de8493f20734", ID_XSLT_MXML_TO_TEI);
    assertEquals("fe5f8d4f-ed82-3499-ab72-ef6029aa18b1", SchemaResourceFileService.ID_XSLT_TEI_TO_TEI);
    assertEquals("83f0643b-af56-3cfc-8c0d-bfaf99c96e24", SchemaResourceFileService.ID_XSLT_HSP_TO_TEI);
    assertEquals("19985529-2374-3519-a5e7-0c11a39548ce", SchemaResourceFileService.ID_XSLT_TEI_TO_HSP);
    assertEquals("1fe6e06f-2337-386c-9ef4-fee6ef946069", SchemaResourceFileService.ID_ISOSCH_TEI_ALL);
    assertEquals("bf12cfa2-c082-36a5-b99f-2ee08344def8", SchemaResourceFileService.ID_ISOSCH_TEI_HSP);
    assertEquals("79c6f3ca-f669-3298-ab73-558686e63ffc", SchemaResourceFileService.ID_RNG_TEI_ALL);
    assertEquals("1c4fda8b-b952-34b0-8e2b-c4a65357c4f4", SchemaResourceFileService.ID_RNG_TEI_HSP);
    assertEquals("c6571b64-f3f0-30d9-8ae0-1fceeb2027ea", ID_XSD_MARC21);
    assertEquals("92d8235e-e8df-33cd-9ee1-0e2b4e8d1549", SchemaResourceFileService.ID_XSD_MXML);
    assertEquals("c63e4c80-29ae-3750-9ede-c5a3084c8dc1", SchemaResourceFileService.ID_XSD_TEI_ALL);
    assertEquals("d98b97fe-2718-3b80-93c6-7f1213f21a33", SchemaResourceFileService.ID_XSD_TEI_ALL_DCR);
    assertEquals("6672ec9d-b3d8-3339-9dbc-84ac0f522061", SchemaResourceFileService.ID_XSD_TEI_ALL_TEIX);
    assertEquals("6a0afe6c-5574-3c2d-ac22-a1607f7cd62b", SchemaResourceFileService.ID_XSD_TEI_ALL_XML);
  }

  @Test
  void testFindAll() {
    Iterable<SchemaResourceFile> result = schemaResourceFileService.findAll();

    assertNotNull(result);
    assertEquals(SCHEMA_RESOURCE_IDS.length, StreamSupport.stream(result.spliterator(), false).count());
  }

  @Test
  void testFindById() throws IOException {
    Optional<SchemaResourceFile> result = schemaResourceFileService.findOptionalById(
        ID_XSLT_MXML_TO_TEI);

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(ID_XSLT_MXML_TO_TEI, result.get().getId());
    assertEquals("MXML-to-TEI-P5.xsl", result.get().getDateiName());
    assertEquals(SchemaResourceTyp.XSLT, result.get().getSchemaResourceTyp());
    assertEquals(XMLFormate.MXML, result.get().getXmlFormat());
    assertEquals("system", result.get().getBearbeitername());
    assertNotNull(result.get().getDatei());
    assertTrue(result.get().getDatei().isPresent());
    assertTrue(result.get().getDatei().get().toString().contains("MXML-to-TEI-P5.xsl"));
    assertNotNull(result.get().getErstellungsDatum());
    assertNotNull(result.get().getAenderungsDatum());
  }

  @Test
  @Transactional
  void testSave() throws Exception {
    String id = ID_XSLT_MXML_TO_TEI;
    byte[] datei = "test".getBytes(StandardCharsets.UTF_8);
    String bearbeitername = "junit";
    Version version = Version.parse("9.99.99");

    Exception exception = assertThrows(Exception.class,
        () -> schemaResourceFileService.save(id, datei, bearbeitername, null));
    assertEquals("Version is required!", exception.getMessage());

    schemaResourceFileService.save(id, datei, bearbeitername, version);

    Optional<SchemaResourceFile> result = schemaResourceFileService.findOptionalById(
        ID_XSLT_MXML_TO_TEI);
    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(ID_XSLT_MXML_TO_TEI, result.get().getId());
    assertEquals(bearbeitername, result.get().getBearbeitername());
    assertEquals(version, result.get().getVersion());
    assertNotNull(result.get().getDatei());
    assertTrue(result.get().getDatei().isPresent());
    assertTrue(result.get().getDatei().get().getURL().toString().endsWith("MXML-to-TEI-P5.xsl"));
  }

  @Test
  void testReadClasspathResources() {
    SchemaResourceFileService service = ((SchemaResourceFileService) schemaResourceFileService);
    Map<String, SchemaResourceFile> resources = service.readClasspathResources();

    assertNotNull(resources);
    assertEquals(SCHEMA_RESOURCE_IDS.length, resources.size());
    assertTrue(resources.containsKey(ID_XSLT_MXML_TO_TEI));
    SchemaResourceFile schemaResourceFile = resources.get(ID_XSLT_MXML_TO_TEI);
    assertNotNull(schemaResourceFile);
    assertEquals(SchemaResourceTyp.XSLT, schemaResourceFile.getSchemaResourceTyp());
    assertEquals(XMLFormate.MXML, schemaResourceFile.getXmlFormat());
    assertNotNull(schemaResourceFile.getDatei());
    assertNotNull(schemaResourceFile.getAenderungsDatum());
    assertNotNull(schemaResourceFile.getErstellungsDatum());
    assertEquals("MXML-to-TEI-P5.xsl", schemaResourceFile.getDateiName());
  }

  @Test
  void testCreateSchemaResourceFile() throws Exception {
    SchemaResourceFileService service = ((SchemaResourceFileService) schemaResourceFileService);

    SchemaResourceFile result = schemaResourceFileService.findOptionalById(
        ID_XSLT_MXML_TO_TEI).orElseThrow();

    Path testFile = Paths.get("/tmp/hsp-schemaresources", result.getId(), "xslt", "mxml", "MXML-to-TEI-P5.xsl");
    SchemaResourceFile schemaResourceFile = service.createSchemaResourceFile(new FileSystemResource(testFile));

    assertNotNull(schemaResourceFile);
    assertEquals(SchemaResourceTyp.XSLT, schemaResourceFile.getSchemaResourceTyp());
    assertEquals(XMLFormate.MXML, schemaResourceFile.getXmlFormat());
    assertNotNull(schemaResourceFile.getDatei());
    assertTrue(schemaResourceFile.getDatei().isPresent());
    assertTrue(schemaResourceFile.getDatei().get().getURL().toString().endsWith("MXML-to-TEI-P5.xsl"));
    assertNotNull(schemaResourceFile.getAenderungsDatum());
    assertNotNull(schemaResourceFile.getErstellungsDatum());
    assertEquals("system", schemaResourceFile.getBearbeitername());
  }

  @Test
  void testCopyAndSaveResourceFile() throws IOException {
    SchemaResourceFileService service = ((SchemaResourceFileService) schemaResourceFileService);

    Version newVersion = Version.parse("1.0.1");

    SchemaResourceFile schemaResourceFile = schemaResourceFileService.findOptionalById(ID_XSD_MARC21)
        .orElseThrow();

    schemaResourceFile.setVersion(newVersion);

    service.copyAndSaveResourceFile(schemaResourceFile);

    schemaResourceFile = schemaResourceFileService.findOptionalById(ID_XSD_MARC21)
        .orElseThrow();

    assertEquals(newVersion, schemaResourceFile.getVersion());

    String resourcePath = service.createResourcePath(schemaResourceFile);
    assertNotNull(resourcePath);
    assertTrue(resourcePath.contains("hsp-schemaresources/1.0.1/xsd/marc21/MARC21slim.xsd"));

    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource fileResource = resolver.getResource(resourcePath);

    assertTrue(Files.exists(fileResource.getFile().toPath()));
  }

  @Test
  void testDelete() throws IOException {
    SchemaResourceFile schemaResourceFile = SchemaResourceFile.builder()
        .withId("123")
        .withXmlFormat(XMLFormate.MXML)
        .withSchemaResourceTyp(SchemaResourceTyp.XSLT)
        .withDateiName("test.name")
        .withBearbeitername("system")
        .withVersion(Version.parse("1.0.1"))
        .build();

    SchemaResourceFileService service = ((SchemaResourceFileService) schemaResourceFileService);

    String resourcePath = service.createResourcePath(schemaResourceFile);
    assertNotNull(resourcePath);
    assertTrue(resourcePath.contains("hsp-schemaresources/1.0.1/xslt/mxml/test.name"));

    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource fileResource = resolver.getResource(resourcePath);

    File resourceFile = fileResource.getFile();
    Path resourceFilePath = resourceFile.toPath();
    Files.createDirectories(resourceFilePath.getParent());
    Files.write(resourceFilePath, "test".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);

    schemaResourceFile.setDatei(Optional.of(fileResource));

    assertTrue(Files.exists(resourceFilePath));
    service.delete(schemaResourceFile);
    assertFalse(Files.exists(resourceFilePath));
  }

}

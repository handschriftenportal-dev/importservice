package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 27.03.2019.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class FileServiceTest {

  @Autowired
  private FileService fileService;

  @Value("${import.datadirectory}")
  private String dataDirectory;

  @BeforeEach
  void setUp() {
    if (!Paths.get(dataDirectory).toFile().exists()) {
      Paths.get(dataDirectory).toFile().mkdir();
    }
  }

  @Test
  public void testSave() throws Exception {
    final ImportJob job = new ImportJob(dataDirectory, "test.test", "test");
    String jobName = job.getName();
    final ImportFile initialImportFile = ImportFile.builder().id(UUID.randomUUID().toString())
        .dateiName(jobName)
        .path(new File(job.getImportDir() + File.separator + jobName).toPath())
        .dateiFormat(DateiFormate.UNBEKANNT).error(false)
        .message(null)
        .build();

    job.getImportFiles().add(initialImportFile);
    byte[] content = "Test".getBytes();

    fileService.save(job.getImportFiles().iterator().next(), content);

    File file = new File(dataDirectory + File.separator + jobName);
    byte[] result = Files.readAllBytes(file.toPath());

    Assertions.assertEquals("Test", new String(result));

    Files.delete(file.toPath());
  }

  @ParameterizedTest
  @ValueSource(strings = {"test.zip", "tei-msDesc_Koch.xml"})
  void testIdentifyFileType(String filename) throws Exception {

    Path resultPath = ResourceUtils.getFile("classpath:" + filename).toPath();

    String type = fileService.identifyFileType(resultPath);

    Set<String> knownType = new HashSet(
        Arrays.asList("application/x-zip-compressed", "text/xml", "application/zip",
            "application/xml"));

    Assertions.assertTrue(knownType.contains(type), type);
  }

  @Test
  public void testUnzip() throws Exception {
    final ImportJob job = createFileAndJob("test.zip");

    List<ImportFile> result = fileService.unzip(job.getImportFiles().iterator().next(),
        job.getImportDir());

    Assertions.assertEquals(7, result.size());

    for (ImportFile importFile : job.getImportFiles()) {
      Files.delete(importFile.getPath());
    }
  }

  @Test
  public void testRemoveBOM() throws Exception {
    final ImportJob job = createFileAndJob("test-with-bom.xml");

    fileService.removeBOM(job.getImportFiles().iterator().next());

    Path resultPath = new File(
        dataDirectory + File.separator + "test-with-bom-wthout-BOM.xml").toPath();
    byte[] result = Files.readAllBytes(resultPath);
    Assertions.assertEquals("Test", new String(result));

    Files.delete(resultPath);
  }

  @Test
  void testIsAllowedContentType() throws IOException {
    File file = ResourceUtils.getFile("classpath:alto-B_SB_Kat_6_1_0001-IDs-TEI.xml");

    MockMultipartFile datei = new MockMultipartFile(
        "alto-B_SB_Kat_6_1_0001-IDs-TEI.xml",
        "alto-B_SB_Kat_6_1_0001-IDs-TEI.xml",
        "application/xml",
        new FileInputStream(file));

    Assertions.assertTrue(fileService.isAllowedContentType(datei.getContentType()));
  }

  @Test
  void testSaveStringParts() throws IOException {
    File file = new File(dataDirectory + File.separator + "test.test");

    fileService.saveStringParts(file.getAbsolutePath(), new String[]{"test", "123"});
    Assertions.assertEquals(7, file.length());

    Files.deleteIfExists(file.toPath());
  }

  private ImportJob createFileAndJob(final String filename) throws Exception {
    ImportJob result = null;

    final File file = new File(dataDirectory + File.separator + filename);
    Files.copy(ResourceUtils.getFile("classpath:" + filename).toPath(),
        file.toPath(),
        StandardCopyOption.REPLACE_EXISTING);

    result = new ImportJob(dataDirectory, "test.zip", "test");
    result.getImportFiles()
        .add(ImportFile.builder().id(UUID.randomUUID().toString())
            .dateiName("test.zip")
            .path(file.toPath())
            .dateiFormat(DateiFormate.UNBEKANNT).error(false)
            .message(null)
            .build());

    return result;
  }
}

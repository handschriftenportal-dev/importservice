package staatsbibliothek.berlin.hsp.importservice.persistence;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class ImportJobRepositoryTest {

  @Autowired
  private ImportJobRepository importJobRepository;

  @BeforeEach
  void setup() {
    importJobRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    importJobRepository.deleteAll();
  }

  @Test
  void testImportJob_SaveLargeErrorMessage() {
    ImportJob importJob = new ImportJob("/dev/null", "test.xml", "Tester",
        "KOD", TotalResult.FAILED, StringUtils.repeat("x", 4096 * 2));

    assertTrue(importJob.getErrorMessage().length() > 4096);

    importJobRepository.save(importJob);

    assertTrue(importJobRepository.findById(importJob.getId()).isPresent());
  }

  @Test
  void testDeleteByIdsWithEmpty() {
    importJobRepository.deleteAllById(List.of());
    Assertions.assertEquals(0, importJobRepository.count());
  }

  @Test
  void testDeleteById() {
    ImportJob importJob = new ImportJob("/dev/null", "test.xml", "Tester",
        "KOD", TotalResult.FAILED, StringUtils.repeat("x", 4096 * 2));
    importJobRepository.save(importJob);
    assertTrue(importJobRepository.existsById(importJob.getId()));
    importJobRepository.deleteById(importJob.getId());
    assertFalse(importJobRepository.existsById(importJob.getId()));
  }
}

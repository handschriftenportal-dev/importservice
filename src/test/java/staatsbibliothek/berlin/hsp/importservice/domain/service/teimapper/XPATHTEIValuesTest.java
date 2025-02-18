package staatsbibliothek.berlin.hsp.importservice.domain.service.teimapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 15.11.2019.
 * @version 1.0
 */
@SpringBootTest
public class XPATHTEIValuesTest {

  @Autowired
  private XPATHTEIValues xpathteiValues;

  @Test
  void testCreation() {
    assertNotNull(xpathteiValues);

    assertEquals("//msDesc//source/ancestor::msDesc", xpathteiValues.getBeschreibungDocuments());
  }
}

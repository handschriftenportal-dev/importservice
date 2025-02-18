package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 10.06.2020.
 * @version 1.0
 */
public class ImportEntityDataTest {

  @Test
  void testCreation() {

    ImportEntityData entityData = new ImportEntityData("123", "label", null);

    assertNotNull(entityData);
    assertNotNull(entityData.getId());
    assertNotNull(entityData.getLabel());
  }

  @Test
  void testEquals() {
    ImportEntityData entityData = new ImportEntityData("123", "label", null);
    ImportEntityData entityData1 = new ImportEntityData("1234", "label", null);

    assertNotEquals(entityData, entityData1);
  }
}

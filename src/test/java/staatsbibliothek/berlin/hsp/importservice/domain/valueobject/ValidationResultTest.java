package staatsbibliothek.berlin.hsp.importservice.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.ValidationResult.ValidationDetails;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 19.07.23.
 * @version 1.0
 */
public class ValidationResultTest {

  @Test
  void testCreation() {

    ValidationResult validationResult = new ValidationResult();

    assertNotNull(validationResult);
    assertNotNull(validationResult.getDetails());
  }

  @Test
  void testValidationDetails() {
    ValidationResult validationResult = new ValidationResult();
    validationResult.addDetail("XPATH", "ERROR");

    assertEquals(1, validationResult.getDetails().size());
    assertEquals("XPATH", validationResult.getDetails().get(0).getXpath());
    assertEquals("ERROR", validationResult.getDetails().get(0).getError());
  }

  @Test
  void testValidationDetailsTranslation() {
    ValidationResult validationResult = new ValidationResult();
    ValidationDetails details = validationResult.addDetail("XPATH", "ERROR");

    details.appendDiagnostics("en", "When is needed");

    assertEquals("When is needed", details.getDiagnostics().get(0).getMessage());
  }

  @Test
  void testgetMessageByLanguageCode() {
    ValidationResult validationResult = new ValidationResult();
    ValidationDetails details = validationResult.addDetail("XPATH", "ERROR");

    details.appendDiagnostics("en", "When is needed");
    details.appendDiagnostics("de", "When Angabe benötigt");

    Assertions.assertEquals("When Angabe benötigt", details.getMessageByLanguageCode("de"));
  }
}

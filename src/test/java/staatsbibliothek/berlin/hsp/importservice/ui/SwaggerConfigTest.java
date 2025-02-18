package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 06.09.2019.
 */

@SpringBootTest
public class SwaggerConfigTest {

  @Autowired
  private SwaggerConfig swaggerConfig;

  @Test
  void testGivenApplicationContextCreation() {

    assertNotNull(swaggerConfig);

  }

  @Test
  void testApiInfoCreation() {

    OpenAPI apiInfo = swaggerConfig.importAPIDescription();

    assertNotNull(apiInfo);

    assertEquals(apiInfo.getInfo().getTitle(), "Handschriftenportal Importservice REST Interface");
  }
}

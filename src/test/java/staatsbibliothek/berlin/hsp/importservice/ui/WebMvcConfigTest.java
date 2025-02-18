package staatsbibliothek.berlin.hsp.importservice.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 06.09.2019.
 */
@SpringBootTest
public class WebMvcConfigTest {

  @Autowired
  private WebMvcConfig webMvcConfig;

  @Test
  void testGivenApplicationContextCreation() {

    assertNotNull(webMvcConfig);
  }

  @Test
  void testGetMessageResource() {

    MessageSource messageSource = webMvcConfig.getMessageResource();

    assertNotNull(messageSource);

    assertEquals("Handschriftenportal Datenimport",
        messageSource.getMessage("label.import.title", null, null, Locale.GERMAN));
  }

  @Test
  void testCreateLocaleResolver() {
    LocaleResolver localeResolver = webMvcConfig.localeResolver();

    assertNotNull(localeResolver);
  }

  @Test
  void testCreationLocaleInterceptor() {

    LocaleChangeInterceptor localeChangeInterceptor = webMvcConfig.localeInterceptor();

    assertNotNull(localeChangeInterceptor);

    assertEquals("lang", localeChangeInterceptor.getParamName());
  }
}

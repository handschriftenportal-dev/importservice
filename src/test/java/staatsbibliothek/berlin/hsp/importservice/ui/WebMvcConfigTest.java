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
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

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */

@SpringBootTest()
public class MVCErrorControllerTest {

  @Autowired
  private MVCErrorController controller;

  @MockBean
  private HttpServletRequest request;

  @Test
  void testhandleUploadError() {

    Assertions.assertNotNull(controller);

    StringBuffer path = new StringBuffer("http://localhost:9296/import");

    Mockito.when(request.getRequestURL())
        .thenReturn(path);

    Exception exception = new Exception("Test");

    ModelAndView result = controller.handleUploadError(exception, request);

    Assertions.assertEquals("/error", result.getViewName());
    Assertions.assertEquals(exception, result.getModel().get("exception"));
    Assertions.assertEquals(path,
        result.getModel().get("path"));

  }

  @Test
  void testhandleGlobalError() {

    StringBuffer path = new StringBuffer("http://localhost:9296/import");

    Mockito.when(request.getRequestURL())
        .thenReturn(path);

    Exception exception = new Exception("Test");

    ModelAndView result = controller.handleGlobalError(exception, request);

    Assertions.assertEquals("/error", result.getViewName());
    Assertions.assertEquals(exception, result.getModel().get("exception"));
    Assertions.assertEquals(path,
        result.getModel().get("path"));

  }
}

package staatsbibliothek.berlin.hsp.importservice.ui;

import jakarta.servlet.http.HttpServletRequest;
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

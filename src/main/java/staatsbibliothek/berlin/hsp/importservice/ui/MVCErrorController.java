package staatsbibliothek.berlin.hsp.importservice.ui;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 25.03.2019.
 */

@ControllerAdvice
public class MVCErrorController {

  private static final Logger logger = LoggerFactory.getLogger(MVCErrorController.class);

  @ExceptionHandler(value = {MultipartException.class, MaxUploadSizeExceededException.class,
      IOException.class})
  public ModelAndView handleUploadError(Exception e, HttpServletRequest req) {

    logger.info("Error during file upload ", e);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", e);
    mav.addObject("path", req.getRequestURL());
    mav.addObject("message", "Beim Hochladen der Datei ist ein Fehler aufgetreten.");

    mav.setViewName("/error");

    return mav;
  }

  @ExceptionHandler(value = {Exception.class, RuntimeException.class})
  public ModelAndView handleGlobalError(Exception e, HttpServletRequest req) {

    logger.info("Global Error occurred ", e);

    ModelAndView mav = new ModelAndView();
    mav.addObject("exception", e);
    mav.addObject("path", req.getRequestURL());
    mav.addObject("message", "Ein Fehler ist aufgetreten");

    mav.setViewName("/error");

    return mav;
  }

}

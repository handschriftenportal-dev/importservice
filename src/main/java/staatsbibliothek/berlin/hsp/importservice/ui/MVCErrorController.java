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

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
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

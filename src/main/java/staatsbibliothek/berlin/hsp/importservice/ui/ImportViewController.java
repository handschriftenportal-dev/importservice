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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.DateiImportBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@CrossOrigin(origins = "*")
@Controller
@Slf4j
public class ImportViewController {

  private static final List<String> benutzer = Arrays.asList(
      "Robert Giel",
      "Torsten Schassan",
      "Christoph Mackert",
      "Malgorzata Asch",
      "Konrad Eichstädt");

  private static final String JOB_LIST_ATTRIBUTE = "importjobs";
  private static final String FILE_LIST_ATTRIBUTE = "importfiles";

  private DateiBoundary fileService;

  private DateiImportBoundary dateiImportBoundary;

  private ImportJobRepository importJobRepository;

  private Set<ImportFile> latestFiles;

  private String latestErrorMessage;

  @Autowired
  public ImportViewController(DateiBoundary fileService,
      @Qualifier("BeschreibungsImport") DateiImportBoundary dateiImportBoundary,
      ImportJobRepository importJobRepository) {
    this.fileService = fileService;
    this.dateiImportBoundary = dateiImportBoundary;
    this.importJobRepository = importJobRepository;
  }

  @GetMapping("/app/**")
  public ModelAndView getWebcomponentPage() {

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/app");

    return modelAndView;
  }

  @GetMapping("/import")
  public ModelAndView getImportPage() {
    log.info("Request for import seite.");

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModelMap().addAttribute(JOB_LIST_ATTRIBUTE, importJobRepository.findAll());

    if (latestFiles != null) {
      modelAndView.getModelMap().addAttribute(FILE_LIST_ATTRIBUTE, new ArrayList<>(latestFiles));
      latestFiles = null;
    }

    if (latestErrorMessage != null && !latestErrorMessage.isEmpty()) {
      modelAndView.getModelMap().addAttribute("message", latestErrorMessage);
      latestErrorMessage = "";
    }

    modelAndView.setViewName("/import");

    return modelAndView;
  }

  @GetMapping("/import/job/{id}")
  public ModelAndView getImportJobPage(@PathVariable(name = "id") String jobid) {
    log.info("Request for import job seite with id {}", jobid);

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.setViewName("/job");
    modelAndView.getModelMap().addAttribute(FILE_LIST_ATTRIBUTE, latestFiles);

    Optional<ImportJob> job = importJobRepository.findById(jobid);

    if (job.isPresent()) {
      modelAndView.getModelMap().addAttribute("job", job.get());
    }

    return modelAndView;
  }

  @PostMapping("/import")
  public ModelAndView handleDateiUpload(@RequestParam("datei") MultipartFile datei, HttpServletRequest request)
      throws IOException {
    log.info("POST Request for import Datei Upload from  {} ", request.getHeader("referer"));

    if (fileService.isSupportedContent(datei)) {

      try {
        ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder().withType(ActivityStreamsDokumentTyp.BESCHREIBUNG).withContent(datei.getBytes()).build();

        ImportJob importJob = dateiImportBoundary
            .importDateien(activityStreamObject, datei.getOriginalFilename(), benutzer.get(new Random().nextInt(4)), ActivityStreamsDokumentTyp.BESCHREIBUNG);

        latestFiles = importJob.getImportFiles();

      } catch (ActivityStreamsException e) {
        log.error("ActivityStreamsException error ", e);
      }

    } else {

      latestErrorMessage = "Datei oder Dateiformat wird nicht unterstützt!";
    }

    ModelAndView modelAndView = new ModelAndView();
    modelAndView.getModelMap().addAttribute(JOB_LIST_ATTRIBUTE, importJobRepository.findAll());
    modelAndView.getModelMap().addAttribute(FILE_LIST_ATTRIBUTE, new ArrayList<>(latestFiles));
    modelAndView.setView(new RedirectView(request.getHeader("referer")));

    return modelAndView;
  }

}
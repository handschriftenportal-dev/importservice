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

package staatsbibliothek.berlin.hsp.importservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamObjectTagId;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.HSPActivityStreamObjectTag;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.kafka.KafkaNachweisProducer;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 28.03.2019.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class BeschreibungenImportServiceTest {

  @Autowired
  private BeschreibungenImportService beschreibungenImportService;

  @MockBean
  private KafkaNachweisProducer kafkaNachweisProducer;

  @Autowired
  private MessageSource messageSource;

  @Test
  public void testImportDateienTEISuccess() throws Exception {
    String filename = "tei-msDesc_Lesser.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
        .withTag(List.of(
            new HSPActivityStreamObjectTag("boolean", ActivityStreamObjectTagId.DEACTIVATE_HSP_FORMAT_VALIDATION,
                "true")))
        .withContent(content).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());
    Assertions.assertFalse(job.getImportFiles().iterator().next().isError());

    Assertions.assertTrue(jobContainsImportFile(job,
        "tei-msDesc_Lesser-wthout-BOM-translated.xml", false, null));

    Assertions.assertEquals(TotalResult.IN_PROGRESS, job.getResult());

    Mockito.verify(kafkaNachweisProducer, Mockito.times(1))
        .sendMessage(Mockito.any(), Mockito.any());
  }

  @Test
  public void testImportDateienMXMLSuccess() throws Exception {
    String filename = "MXMLDokument.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG).withContent(content).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());
    Assertions.assertFalse(job.getImportFiles().iterator().next().isError());

    Assertions.assertTrue(jobContainsImportFile(job,
        "MXMLDokument-wthout-BOM-translated.xml", false, null));

    Assertions.assertEquals(TotalResult.IN_PROGRESS, job.getResult());

    Mockito.verify(kafkaNachweisProducer, Mockito.times(1))
        .sendMessage(Mockito.any(), Mockito.any());
  }

  @Test
  public void testImportDateienTEISuccessWithTagAndError() throws Exception {
    String filename = "tei-msDesc_LesserHSPValidation.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObjectTag activityStreamObjectTag = new HSPActivityStreamObjectTag("boolean",
        ActivityStreamObjectTagId.DEACTIVATE_HSP_FORMAT_VALIDATION, "false");

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
        .withContent(content)
        .withTag(List.of(activityStreamObjectTag)).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());
    Assertions.assertTrue(job.getImportFiles().iterator().next().isError());

    Assertions.assertTrue(jobContainsImportFile(job,
        "tei-msDesc_LesserHSPValidation-wthout-BOM-translated.xml", true, messageSource
            .getMessage("import.error.validate.xml.line.number", null,
                LocaleContextHolder.getLocale())));

    Assertions.assertEquals(
        "Eine oder mehrere Dateien sind fehlerhaft: tei-msDesc_LesserHSPValidation.xml: Zeilennummer: 2 Spaltennummer: 216 Fehlernachricht: element \"titleStmt\" not allowed here; expected element \"teiHeader\"",
        job.getErrorMessage());
  }


  @Test
  public void testImportDateienTEISuccessWithTag() throws Exception {
    String filename = "tei-msDesc_hsp_bare.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObjectTag activityStreamObjectTag = new HSPActivityStreamObjectTag("boolean",
        ActivityStreamObjectTagId.DEACTIVATE_HSP_FORMAT_VALIDATION, "true");

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
        .withContent(content)
        .withTag(List.of(activityStreamObjectTag)).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());
    Assertions.assertFalse(job.getImportFiles().iterator().next().isError());

    Assertions.assertTrue(jobContainsImportFile(job,
        "tei-msDesc_hsp_bare-wthout-BOM-translated.xml", false, null));

    Assertions.assertEquals(TotalResult.IN_PROGRESS, job.getResult());

    Mockito.verify(kafkaNachweisProducer, Mockito.times(1))
        .sendMessage(Mockito.any(), Mockito.any());
  }

  @Test
  public void testImportDateienTEISuccessWithExternTag() throws Exception {
    String filename = "tei-msDesc_hsp_bare.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObjectTag activityStreamObjectTag = new HSPActivityStreamObjectTag("String",
        ActivityStreamObjectTagId.INTERN_EXTERN, "EXTERN");

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
        .withContent(content)
        .withTag(List.of(activityStreamObjectTag)).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());
    Assertions.assertFalse(job.getImportFiles().iterator().next().isError());

    Assertions.assertTrue(jobContainsImportFile(job,
        "tei-msDesc_hsp_bare-wthout-BOM-translated.xml", false, null));

    Assertions.assertEquals(TotalResult.IN_PROGRESS, job.getResult());

    ArgumentCaptor<Optional<ActivityStreamObjectTag>> internExternTag = ArgumentCaptor.forClass(Optional.class);

    Mockito.verify(kafkaNachweisProducer, Mockito.times(1))
        .sendMessage(Mockito.any(), internExternTag.capture());

    Optional<ActivityStreamObjectTag> capturedInternExternTag = internExternTag.getValue();
    assertNotNull(capturedInternExternTag);
    assertTrue(capturedInternExternTag.isPresent());
    assertEquals("EXTERN", capturedInternExternTag.get().getName());
    assertEquals(ActivityStreamObjectTagId.INTERN_EXTERN.toString(), capturedInternExternTag.get().getId());
    assertEquals("String", capturedInternExternTag.get().getType());
  }

  @Test
  // TODO ticket #16137
  @Disabled
  public void testImportDateienComplexZIPFailure() throws Exception {
    String filename = "test.zip";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withTag(List.of(
            new HSPActivityStreamObjectTag("boolean", ActivityStreamObjectTagId.DEACTIVATE_HSP_FORMAT_VALIDATION,
                "true")))
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG).withContent(content).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(7, job.getImportFiles().size());

    Assertions.assertTrue(jobContainsImportFile(job,
        "tei_msDesc_Heinemann-wthout-BOM-translated.xml", true,
        messageSource
            .getMessage("import.error.validate.xml.line.number", null,
                LocaleContextHolder.getLocale())));
    Assertions.assertTrue(jobContainsImportFile(job,
        "BV021690266-wthout-BOM-translated.xml", true,
        messageSource.getMessage("import.error.xslt", null, LocaleContextHolder.getLocale())));
    Assertions.assertTrue(jobContainsImportFile(job,
        "tei-msDesc_Lesser-wthout-BOM-translated.xml", false, null));
    //TODO: Must be fixed within MXML Document for this testcase Spring 25 pressure
    Assertions.assertTrue(jobContainsImportFile(job,
        "MXMLDokument-wthout-BOM-translated.xml", false, null));
    Assertions.assertTrue(jobContainsImportFile(job,
        "tei_msDesc_Heinemann-alternate-wthout-BOM-translated.xml", false, null));

    Assertions.assertEquals(TotalResult.FAILED, job.getResult());
    Assertions.assertEquals(
        "Eine oder mehrere Dateien sind fehlerhaft: BV021690266.xml: Datei konnte nicht übersetzt werden (XSLT).; "
            + "tei_msDesc_Heinemann.xml: Zeilennummer: 150 Spaltennummer: 15049 Fehlernachricht: element \"textLang\" not allowed here; "
            + "expected the element end-tag or element \"msItem\", \"msItemStruct\" or \"titlePage\"",
        job.getErrorMessage());

    Mockito.verify(kafkaNachweisProducer, Mockito.never())
        .sendMessage(Mockito.any(), Mockito.any());
  }

  @Test
  public void testImportDateienMARC21Failure() throws Exception {
    String filename = "BV021690266.xml";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG).withContent(content).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());

    Assertions.assertEquals(TotalResult.FAILED, job.getResult());
    Assertions.assertEquals(
        "Eine oder mehrere Dateien sind fehlerhaft: BV021690266.xml: Datei konnte nicht übersetzt werden (XSLT).",
        job.getErrorMessage());

    Mockito.verify(kafkaNachweisProducer, Mockito.never())
        .sendMessage(Mockito.any(), Mockito.any());
  }

  @Test
  public void testImportDateienZIPUnknownFileFailure() throws Exception {
    String filename = "test-with-bom.zip";

    File file = ResourceUtils.getFile("classpath:" + filename);
    byte[] content = Files.readAllBytes(file.toPath());

    ActivityStreamObject activityStreamObject = HSPActivityStreamObject.builder()
        .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG).withContent(content).build();

    final ImportJob job = beschreibungenImportService
        .importDateien(activityStreamObject, filename, "Konrad",
            ActivityStreamsDokumentTyp.BESCHREIBUNG);

    assertNotNull(job);
    Assertions.assertEquals(1, job.getImportFiles().size());

    Assertions.assertTrue(jobContainsImportFile(job,
        "test-with-bom-wthout-BOM.xml", true,
        messageSource
            .getMessage("import.error.apply.format", null, LocaleContextHolder.getLocale())));

    Assertions.assertEquals(TotalResult.FAILED, job.getResult());
    Assertions.assertEquals(
        "Eine oder mehrere Dateien sind fehlerhaft: test-with-bom.xml: Format konnte nicht ermittelt werden.",
        job.getErrorMessage());

    Mockito.verify(kafkaNachweisProducer, Mockito.never())
        .sendMessage(Mockito.any(), Mockito.any());
  }

  private boolean jobContainsImportFile(final ImportJob job, String fileName, final boolean error,
      final String message) {
    boolean result = false;

    for (ImportFile importFile : job.getImportFiles()) {

      if (fileName.equals(importFile.getPath().toFile().getName())) {
        if (error && importFile.getMessage().contains(message)) {
          result = true;
        } else if (!error && Objects.isNull(importFile.getMessage())) {
          result = true;
        }
      }
    }

    return result;
  }

}
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

import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_ISOSCH_TEI_ALL;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_ISOSCH_TEI_HSP;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_RNG_TEI_ALL;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_RNG_TEI_HSP;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_XSLT_HSP_TO_TEI;
import static staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary.ID_XSLT_TEI_TO_HSP;

import com.thaiopensource.util.PropertyMap;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.Flag;
import com.thaiopensource.validate.SchemaReader;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.validate.prop.rng.RngProperty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.exception.ApplicationException;
import staatsbibliothek.berlin.hsp.importservice.domain.exception.StopParsingBeforeEndException;
import staatsbibliothek.berlin.hsp.importservice.domain.service.teimapper.XPATHTEIValues;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.ValidationResult;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 01.04.2019.
 */
@Component
@Slf4j
public class XMLService {

  public static final String PROP_TEI_2_HSP_ENABLED = "de.staatsbibliothek.berlin.hsp.messaging.tei2hsp_enabled";

  public static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

  private static final boolean TEI_TO_HSP_ENABLED;

  static {
    System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

    String tei2hsp = System.getProperty(PROP_TEI_2_HSP_ENABLED);
    TEI_TO_HSP_ENABLED = (Objects.nonNull(tei2hsp) && "false".equals(tei2hsp)) ? false : true;

    log.info("TEI_TO_HSP_ENABLED: " + TEI_TO_HSP_ENABLED);
  }

  @Autowired
  XPATHTEIValues xpathteiValues;
  @Autowired
  XMLNamespaceSAXParserHandler handler;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private SchemaResourceFileBoundary schemaResourceFileBoundary;

  private static PropertyMap getPropertyMapWithErrorHandler() {
    ErrorHandler errorHandler = new ErrorHandler() {
      @Override
      public void warning(SAXParseException exception) throws SAXException {
        throw exception;
      }

      @Override
      public void error(SAXParseException exception) throws SAXException {
        throw exception;
      }

      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        throw exception;
      }
    };
    PropertyMapBuilder propertyMapBuilder = new PropertyMapBuilder();
    propertyMapBuilder.put(ValidateProperty.ERROR_HANDLER, errorHandler);
    propertyMapBuilder.put(RngProperty.CHECK_ID_IDREF, Flag.PRESENT);
    return propertyMapBuilder.toPropertyMap();
  }

  void transform(InputStream in, OutputStream out, String schemaResourceFileId)
      throws IOException, TransformerException {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    try (InputStream xsltInputStream = createStreamSource(schemaResourceFileId).getInputStream()) {
      Source xsltSource = new StreamSource(xsltInputStream);
      Transformer transformer = factory.newTransformer(xsltSource);

      StreamSource transformSource = new StreamSource(in);
      StreamResult transformResult = new StreamResult(out);

      transformer.transform(transformSource, transformResult);
    }
  }

  private boolean validateSchemaWithJingLib(String xml, String schemaResourceFileId) throws IOException, SAXException {

    log.info("Start validateSchemaWithJingLib schemaResourceFileId={} ", schemaResourceFileId);

    boolean validationResult;

    try (InputStream schemaFileResource = createStreamSource(schemaResourceFileId).getInputStream()) {
      InputSource inputSource = new InputSource(schemaFileResource);

      SchemaReader autoSchemaReader = new AutoSchemaReader();
      ValidationDriver driver = new ValidationDriver(getPropertyMapWithErrorHandler(),
          autoSchemaReader);
      inputSource.setEncoding("UTF-8");
      driver.loadSchema(inputSource);
      InputSource inXml = new InputSource(new StringReader(xml));
      inXml.setEncoding("UTF-8");
      validationResult = driver.validate(inXml);

      log.info("End validationResult {} ", validationResult);
    }

    return validationResult;
  }

  public void applyXMLFormat(ImportFile importFile) {
    try {
      final File file = importFile.getPath().toFile();

      final XMLFormate format = identifyXMLFormat(file);
      log.info("Import[detectXMLFormat]: format {} detected : {}", format.name(),
          importFile.getPath().toFile().getAbsolutePath());

      importFile.setDateiFormat(format);
    } catch (Exception e) {
      importFile.applyError(getMessage("import.error.apply.format"));
    }
  }

  public void xsltToTEI(ImportFile importFile) {
    final File inFile = importFile.getPath().toFile();
    String orgFileName = importFile.getPath().getFileName().toString();
    final String translatedFileName =
        inFile.getAbsolutePath().substring(0, inFile.getAbsolutePath().length() - 4)
            + "-translated.xml";
    final File outFile = new File(translatedFileName);

    try (final InputStream in = new FileInputStream(inFile);
        final OutputStream out = new FileOutputStream(outFile)) {
      translate(in, out, importFile.getDateiFormat());

      log.info("Import[xsltToTEI]: file translated: {} ( {} -> TEI)", translatedFileName, importFile.getDateiFormat());
    } catch (Exception e) {
      log.error("Error during XSLT Translation!", e);
      importFile.applyError(getMessage("import.error.xslt"));
    }

    importFile.setDateiTyp(orgFileName);
    importFile.setDateiFormat(XMLFormate.TEI_ALL);
    importFile.setPath(new File(translatedFileName).toPath());
  }

  public boolean hspValidateSchema(ImportFile importFile) {
    final File xmlFile = importFile.getPath().toFile();

    log.info("Start hspValidateSchema FileName {}", xmlFile.getName());
    boolean valid = false;

    try {
      String teiString = Files.readString(importFile.getPath());
      valid = validateWithODD(teiString);
      if (!valid) {
        importFile.applyError(getMessage("import.error.xml.not.valid"));
      }
    } catch (SAXParseException saxParseException) {
      log.error("Validation error during Jing validation {}", saxParseException.toString());
      handelSaxParseException(importFile, saxParseException);
    } catch (Exception exception) {
      log.error("Validation error during Jing validation", exception);
      importFile.applyError(getMessage("import.error.validate.xml.jing") + exception.getMessage());
    }

    log.info("HspValidateSchema ends with {}", valid);
    return valid;
  }

  public void validateSchema(ImportFile importFile) {
    final File xmlFile = importFile.getPath().toFile();

    log.info("Start validateSchema FileName {}", xmlFile.getName());

    try {
      SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      factory.setFeature(FEATURE_SECURE_PROCESSING, true);

      Schema schema = null;

      switch (importFile.getDateiFormat()) {
        case MARC21:
          schema = factory.newSchema(createStreamSource(SchemaResourceFileBoundary.ID_XSD_MARC21));
          break;
        case TEI_ALL:
          boolean relaxNGTEIAllValidationResult = validate(Files.readString(importFile.getPath()));
          if (!relaxNGTEIAllValidationResult) {
            importFile.applyError(getMessage("import.error.xml.not.valid"));
          }
          break;
        case MXML:
          schema = factory.newSchema(createStreamSource(SchemaResourceFileBoundary.ID_XSD_MXML));
          break;
        case UNBEKANNT:
          break;
      }

      if (Objects.nonNull(schema)) {
        boolean valid = validateSchema(schema, xmlFile);
        if (!valid) {
          importFile.applyError(getMessage("import.error.xml.not.valid"));
        }
      }
    } catch (SAXParseException saxParseException) {
      log.error("Validation error during XSD validation" + saxParseException);
      handelSaxParseException(importFile, saxParseException);
    } catch (Exception e) {
      log.error("Error during validation", e);
      importFile.applyError(getMessage("import.error.validate.xml") + e.getMessage());
    }
  }

  public boolean isTeiToHspEnabled() {
    return TEI_TO_HSP_ENABLED;
  }

  public boolean validate(String tei) throws SAXException, IOException {
    return validate(tei, TEI_TO_HSP_ENABLED);
  }

  public boolean validate(String tei, boolean transformBeforeValidate) throws SAXException, IOException {
    return validate(tei, transformBeforeValidate, ID_RNG_TEI_ALL, ID_ISOSCH_TEI_ALL);
  }

  public boolean validateWithODD(String tei) throws IOException, SAXException {
    return validateWithODD(tei, TEI_TO_HSP_ENABLED);
  }

  public boolean validateWithODD(String tei, boolean transformBeforeValidate) throws IOException, SAXException {
    return validate(tei, transformBeforeValidate, ID_RNG_TEI_HSP, ID_ISOSCH_TEI_HSP);
  }

  public ValidationResult validateXML(String xmlTEI, boolean validateWithODD, String locale) {
    log.info("validateXML: locale={}, validateWithODD={}", locale, validateWithODD);

    Locale loc = Locale.forLanguageTag(locale);

    ValidationResult validationResult = new ValidationResult(false, "", "",
        getMessage("validation.result.failed", loc));

    try {
      if (validateWithODD) {
        validateWithODD(xmlTEI);
      } else {
        validate(xmlTEI);
      }

      validationResult.setValid(true);
      validationResult.setMessage(getMessage("validation.result.successfull", loc));
    } catch (SAXParseException saxParseException) {
      validationResult
          .setMessage(validationResult.getMessage() + " -> " + saxParseException.getMessage());
      validationResult.setLine(String.valueOf(saxParseException.getLineNumber()));
      validationResult.setColumn(String.valueOf(saxParseException.getColumnNumber()));
      log.debug(saxParseException.getMessage());
    } catch (SAXException | IOException validationException) {
      validationResult
          .setMessage(validationResult.getMessage() + " -> " + validationException.getMessage());
      log.debug(validationException.getMessage());
    }

    log.info("validate ends with validationResult {}", validationResult);
    return validationResult;
  }

  private boolean validate(String tei, boolean transformBeforeValidate, String rngSchemaId, String isoschId)
      throws IOException, SAXException {
    if (transformBeforeValidate) {
      tei = transformHspToTei(tei);
    }

    boolean validationResult = false;
    if (validateSchemaWithJingLib(tei, rngSchemaId)) {
      validationResult = validateSchemaWithJingLib(tei, isoschId);
    }

    return validationResult;
  }

  public void transformTeiToHsp(InputStream in, OutputStream out) throws IOException, TransformerException {
    transform(in, out, ID_XSLT_TEI_TO_HSP);
  }

  public void transformHspToTei(InputStream in, OutputStream out) throws IOException, TransformerException {
    transform(in, out, ID_XSLT_HSP_TO_TEI);
  }

  public String transformHspToTei(String tei) throws IOException {
    if (Objects.isNull(tei)) {
      throw new IOException("tei must not be null!");
    }

    try (InputStream in = new ByteArrayInputStream(tei.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      transformHspToTei(in, out);
      return out.toString();
    } catch (TransformerException tex) {
      throw new IOException("Error transforming HSP to TEI!", tex);
    }
  }

  public String transformTeiToHsp(String tei) throws IOException {
    if (Objects.isNull(tei)) {
      throw new IOException("tei must not be null!");
    }

    try (InputStream in = new ByteArrayInputStream(tei.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      transformTeiToHsp(in, out);
      return out.toString();
    } catch (TransformerException tex) {
      throw new IOException("Error transforming HSP to TEI!", tex);
    }
  }

  private void handelSaxParseException(ImportFile importFile, SAXParseException saxParseException) {
    String saxParseExceptionMessage = MessageFormat
        .format("{0}: {1} {2}: {3}{4} {5}: {6}", getMessage("import.error.validate.xml.line.number"),
            saxParseException.getLineNumber(), getMessage("import.error.validate.xml.column.number"),
            saxParseException.getLineNumber(), saxParseException.getColumnNumber(),
            getMessage("import.error.validate.xml.errormessage"), saxParseException.getMessage());
    importFile.applyError(
        messageSource.getMessage("import.error.validate.xml.sax.parse.exception",
            new String[]{saxParseExceptionMessage}, LocaleContextHolder.getLocale()));
  }

  private StreamSource createStreamSource(String schemaResourceFileId) throws IOException {
    Optional<SchemaResourceFile> resourceFile = schemaResourceFileBoundary.findOptionalById(schemaResourceFileId);
    if (resourceFile.isPresent() && resourceFile.get().getDatei().isPresent()) {
      return new StreamSource(resourceFile.get().getDatei().get().getInputStream());
    } else {
      throw new IOException("No file found for schemaResourceFileId " + schemaResourceFileId);
    }
  }

  boolean validateSchema(final Schema schema, final File xmlFile)
      throws SAXNotRecognizedException, SAXNotSupportedException, SAXParseException {
    boolean result = false;

    Validator validator = schema.newValidator();
    validator.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
    validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

    try (InputStream is = new FileInputStream(xmlFile)) {
      validator.validate(new StreamSource(is));

      result = true;

      log.info("Import[validateXSD]: xml is valid: {}", xmlFile.getAbsolutePath());
    } catch (SAXParseException saxParseException) {
      throw saxParseException;
    } catch (Exception e) {
      log.info("Import[validateXSD]: xml is not valid: {}", xmlFile.getAbsolutePath());
    }

    return result;
  }

  XMLFormate identifyXMLFormat(File file)
      throws ParserConfigurationException, SAXException, IOException {
    XMLFormate result = XMLFormate.UNBEKANNT;

    final SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setFeature(FEATURE_SECURE_PROCESSING, true);
    final SAXParser saxParser = factory.newSAXParser();
    try (FileInputStream fis = new FileInputStream(file)) {
      saxParser.parse(fis, handler);
    } catch (StopParsingBeforeEndException e) {
      result = handler.getFormat();
    }

    return result;
  }

  public void translate(final InputStream in, final OutputStream out, final XMLFormate format)
      throws IOException, TransformerException {
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setURIResolver(new ResourceURIResolver());
    factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

    Source xslt;
    switch (format) {
      case MXML:
        xslt = createStreamSource(SchemaResourceFileBoundary.ID_XSLT_MXML_TO_TEI);
        break;
      case TEI_ALL:
        xslt = createStreamSource(SchemaResourceFileBoundary.ID_XSLT_TEI_TO_TEI);
        break;
      case MARC21:
        log.error("Could not translate MARC21 xml format! (No XSLT Stylesheet yet)");
        throw new NotYetImplementedException();
      default:
        String error = "Could not translate UNBEKANNT xml format!";
        log.error(error);
        throw new ApplicationException(error);
    }
    Transformer transformer = factory.newTransformer(xslt);

    transformer.transform(new StreamSource(in), new StreamResult(out));
  }

  private String getMessage(String key) {
    return getMessage(key, LocaleContextHolder.getLocale());
  }

  private String getMessage(String key, Locale locale) {
    return messageSource.getMessage(key, null, locale);
  }
}
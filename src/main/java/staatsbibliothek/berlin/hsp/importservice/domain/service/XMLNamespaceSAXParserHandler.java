package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import staatsbibliothek.berlin.hsp.importservice.domain.exception.StopParsingBeforeEndException;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by udo.boysen@sbb.spk-berlin.de on 28.01.2020.
 */
@Component
@Slf4j
public class XMLNamespaceSAXParserHandler extends DefaultHandler {

  private static final Map<String, DateiFormate> knownNamespace = new HashMap<>();

  static {
    knownNamespace.put("http://www.loc.gov/MARC21/slim", DateiFormate.MARC21);
    knownNamespace.put("http://www.tei-c.org/ns/1.0", DateiFormate.TEI_ALL);
    knownNamespace
        .put("http://www.startext.de/HiDA/DefService/XMLSchema", DateiFormate.MXML);
  }

  @Getter
  private DateiFormate format = DateiFormate.UNBEKANNT;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    super.startElement(uri, localName, qName, attributes);

    if (knownNamespace.containsKey(uri)) {
      format = knownNamespace.get(uri);
    }

    log.info("Check namespace {} with format {}", uri, format);

    throw new StopParsingBeforeEndException();
  }

}

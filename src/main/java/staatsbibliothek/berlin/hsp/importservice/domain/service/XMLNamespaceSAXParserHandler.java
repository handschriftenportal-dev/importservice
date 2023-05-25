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

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import staatsbibliothek.berlin.hsp.importservice.domain.exception.StopParsingBeforeEndException;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * Created by udo.boysen@sbb.spk-berlin.de on 28.01.2020.
 */
@Component
public class XMLNamespaceSAXParserHandler extends DefaultHandler {

  private static final Map<String, XMLFormate> knownNamespace = new HashMap<>();

  static {
    knownNamespace.put("http://www.loc.gov/MARC21/slim", XMLFormate.MARC21);
    knownNamespace.put("http://www.tei-c.org/ns/1.0", XMLFormate.TEI_ALL);
    knownNamespace
        .put("http://www.startext.de/HiDA/DefService/XMLSchema", XMLFormate.MXML);
  }

  @Getter
  private XMLFormate format = XMLFormate.UNBEKANNT;

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    super.startElement(uri, localName, qName, attributes);

    if (knownNamespace.containsKey(uri)) {
      format = knownNamespace.get(uri);
    }

    throw new StopParsingBeforeEndException();
  }

}

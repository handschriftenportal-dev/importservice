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

package staatsbibliothek.berlin.hsp.importservice.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 06.09.2019.
 */
public class XPATHTest {

  @Test
  void testGivenXMLFilePrepareDocument()
      throws IOException, ParserConfigurationException, SAXException {

    File xmlFile = ResourceUtils.getFile("classpath:Berlin_test_4_190405.xml");

    Document doc = XPATH.prepareDocument(xmlFile, true);

    Assertions.assertNotNull(doc);

    Assertions.assertEquals("UTF-8", doc.getXmlEncoding());
  }

  @Test
  void testGivenXMLFindXMLValueByXPath()
      throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {

    File xmlFile = ResourceUtils.getFile("classpath:SBB_Ms_germ_oct_115.xml");

    Document doc = XPATH.prepareDocument(xmlFile, false);

    String value = XPATH.findXMLValueByXPath(doc, "//Document/@DocKey");

    Assertions.assertEquals("obj     31253152,T", value);
  }

  @Test
  void testGivenNodeFindXMLValueByXPath()
      throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    final File xmlFile = ResourceUtils.getFile("classpath:tei_msDesc_Heinemann.xml");
    final Document doc = XPATH.prepareDocument(xmlFile, false);

    final NodeList nodeList = XPATH.findNodesByXPath(doc, "//msDesc/physDesc/objectDesc/supportDesc");
    final Node node = nodeList.item(0);
    final String value = XPATH.findXMLValueByXPath(node, "/@material | //material/text()");
    Assertions.assertEquals("Pergam.", value);
  }

  @Test
  void testGivenXMLFindNodesByXPath()
      throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
    File xmlFile = ResourceUtils.getFile("classpath:SBB_Ms_germ_oct_115.xml");
    Document doc = XPATH.prepareDocument(xmlFile, false);

    NodeList value = XPATH.findNodesByXPath(doc, "//Field[@Type='bezlit']/@Value");

    Assertions.assertEquals(7, value.getLength());

  }

  @Test
  void testGivenNodeNodeToString()
      throws Exception {

    File xmlFile = ResourceUtils.getFile("classpath:SBB_Ms_germ_oct_115.xml");

    Document doc = XPATH.prepareDocument(xmlFile, false);

    NodeList nodeList = XPATH
        .findNodesByXPath(doc, "//DocumentSet/Document/Block/Field[@Type='5060']");

    String nodeString = XPATH.nodeToString(nodeList.item(0));

    Assertions.assertTrue(nodeString.contains("Datierung"));
  }
}

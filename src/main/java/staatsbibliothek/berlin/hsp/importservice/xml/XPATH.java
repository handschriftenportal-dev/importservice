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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 06.09.2019.
 */
public final class XPATH {

  private XPATH() {
  }

  public static Document prepareDocument(File content, boolean namespaceAware)
      throws IOException, SAXException, ParserConfigurationException {

    try (InputStream inputStream = new FileInputStream(
        content); Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);) {

      InputSource source = new InputSource(reader);

      return readSourceIntoDocument(namespaceAware, source);
    }

  }

  private static Document readSourceIntoDocument(boolean namespaceAware, InputSource source)
      throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
    dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    dbf.setXIncludeAware(false);
    dbf.setExpandEntityReferences(false);
    dbf.setNamespaceAware(namespaceAware);
    DocumentBuilder db;

    db = dbf.newDocumentBuilder();

    return db.parse(source);
  }

  public static String findXMLValueByXPath(Document document, String xpathExpression)
      throws XPathExpressionException {

    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();

    return xpath.evaluate(xpathExpression, document);
  }

  public static String findXMLValueByXPath(final Node node, final String xpathExpression)
      throws XPathExpressionException {
    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();

    return xpath
        .evaluate(xpathExpression, node);
  }

  public static NodeList findNodesByXPath(Document document, String xpathExpression)
      throws XPathExpressionException {
    XPath xPath = XPathFactory.newInstance().newXPath();

    return (NodeList) xPath.compile(xpathExpression)
        .evaluate(document, XPathConstants.NODESET);
  }


  public static String nodeToString(Node node) throws TransformerException, IOException {
    try (StringWriter sw = new StringWriter()) {

      TransformerFactory factory = TransformerFactory.newInstance();
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
      transformer.setOutputProperty(OutputKeys.INDENT, "no");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.transform(new DOMSource(node), new StreamResult(sw));

      return sw.toString();
    }
  }
}
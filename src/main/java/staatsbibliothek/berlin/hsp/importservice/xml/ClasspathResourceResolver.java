package staatsbibliothek.berlin.hsp.importservice.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 03.04.2019.
 */
public class ClasspathResourceResolver implements LSResourceResolver {

  private static final Logger logger = LoggerFactory.getLogger(ClasspathResourceResolver.class);

  @Override
  public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
      String baseURI) {

    logger.debug("Try to find resources {}", systemId);

    LSInputImpl input = new LSInputImpl();
    InputStream stream = getClass().getClassLoader().getResourceAsStream(systemId);
    input.setPublicId(publicId);
    input.setSystemId(systemId);
    input.setBaseURI(baseURI);
    input.setCharacterStream(new InputStreamReader(stream, StandardCharsets.UTF_8));

    return input;
  }

}

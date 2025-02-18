package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.io.IOException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by udo.boysen@sbb.spk-berlin.de on 06.04.2020.
 */
@Slf4j
public class ResourceURIResolver implements URIResolver {

  @Override
  public Source resolve(String href, String base) throws TransformerException {
    Source result;

    try {
      result = new StreamSource(new ClassPathResource(href).getInputStream());
    } catch (IOException e) {
      String error = "Could not resolve href: " + href;
      log.error(error, e);

      throw new TransformerException(error, e);
    }

    return result;
  }

}

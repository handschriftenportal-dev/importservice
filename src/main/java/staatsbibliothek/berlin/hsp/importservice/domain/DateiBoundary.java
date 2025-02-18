package staatsbibliothek.berlin.hsp.importservice.domain;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 08.04.2019.
 */
public interface DateiBoundary {

  boolean isSupportedContent(MultipartFile datei) throws IOException;

}

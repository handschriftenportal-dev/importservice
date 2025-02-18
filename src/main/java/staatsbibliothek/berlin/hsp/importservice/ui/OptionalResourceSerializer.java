package staatsbibliothek.berlin.hsp.importservice.ui;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 21.03.22
 */

public class OptionalResourceSerializer extends StdSerializer<Optional<Resource>> {

  private static final long serialVersionUID = -7661740816993919388L;

  public OptionalResourceSerializer() {
    this(null);
  }

  public OptionalResourceSerializer(Class<Optional<Resource>> optionalResourceClass) {
    super(optionalResourceClass);
  }

  @Override
  public void serialize(Optional<Resource> resource, JsonGenerator jgen, SerializerProvider provider)
      throws IOException {

    if (resource.isPresent()) {
      try (InputStream is = resource.get().getInputStream()) {
        String value = IOUtils.toString(is, StandardCharsets.UTF_8);
        jgen.writeString(value);
      }
    }
  }
}

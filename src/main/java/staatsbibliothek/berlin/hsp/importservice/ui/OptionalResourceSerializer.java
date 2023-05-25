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

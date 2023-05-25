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

package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.module.ModuleDescriptor.Version;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.SchemaResourceTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;
import staatsbibliothek.berlin.hsp.importservice.ui.OptionalResourceSerializer;
import staatsbibliothek.berlin.hsp.importservice.ui.Views;

/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 21.03.22
 */

@Entity
@Data()
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Builder(builderClassName = "SchemaResourceFileBuilder", setterPrefix = "with")
public class SchemaResourceFile {

  @Id
  @JsonProperty("id")
  @JsonView(Views.List.class)
  private String id;

  @Column(name = "xmlFormat", length = 32, nullable = false)
  @Enumerated(EnumType.STRING)
  @JsonProperty("xmlFormat")
  @JsonView(Views.List.class)
  private XMLFormate xmlFormat;

  @Getter
  @Column(name = "schemaResourceTyp", length = 32, nullable = false)
  @Enumerated(EnumType.STRING)
  @JsonProperty("schemaResourceTyp")
  @JsonView(Views.List.class)
  private SchemaResourceTyp schemaResourceTyp;

  @Column(name = "dateiName", length = 256, nullable = false)
  @JsonProperty("dateiName")
  @JsonView(Views.List.class)
  private String dateiName;

  @Column(name = "bearbeitername", length = 64, nullable = false)
  @JsonProperty("bearbeitername")
  @JsonView(Views.List.class)
  private String bearbeitername;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @Column(name = "version", length = 50, nullable = false)
  @JsonProperty("version")
  @JsonView(Views.List.class)
  private String version;

  @Column
  @JsonProperty("erstellungsDatum")
  @JsonView(Views.List.class)
  private LocalDateTime erstellungsDatum;

  @Column
  @JsonProperty("aenderungsDatum")
  @JsonView(Views.List.class)
  private LocalDateTime aenderungsDatum;

  @JsonProperty("datei")
  @JsonSerialize(using = OptionalResourceSerializer.class)
  private transient Optional<Resource> datei = Optional.empty();

  @JsonIgnore
  public Version getVersion() {
    return Objects.nonNull(version) ? Version.parse(version) : null;
  }

  public void setVersion(Version version) {
    this.version = Objects.nonNull(version) ? version.toString() : null;
  }

  public static class SchemaResourceFileBuilder {

    private String version;

    public SchemaResourceFileBuilder withVersion(Version version) {
      Objects.requireNonNull(version, "Version is required");
      this.version = version.toString();
      return this;
    }
  }
}

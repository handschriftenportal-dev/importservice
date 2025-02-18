package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.module.ModuleDescriptor.Version;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;
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
  private DateiFormate xmlFormat;

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

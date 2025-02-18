package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import static lombok.AccessLevel.PACKAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 28.03.2019.
 */
@Entity
@Data
@Builder
@AllArgsConstructor(access = PACKAGE)
public class ImportFile {

  @Id
  private String id;

  @Convert(converter = PathConverter.class)
  private Path path;

  private String dateiTyp;

  private String dateiName;

  private DateiFormate dateiFormat;

  @JsonProperty("importEntityData")
  @Getter
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private final Set<ImportEntityData> importEntityData = new LinkedHashSet<>();
  ;

  private boolean error;

  @Column(length = 4092)
  private String message;

  protected ImportFile() {
    //Needed for JPA , Against the immutability ...
  }

  public void applyError(final String message) {
    error = true;
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImportFile)) {
      return false;
    }
    ImportFile that = (ImportFile) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


  @Override
  public String toString() {
    return new StringJoiner(", ", ImportFile.class.getSimpleName() + "[", "]")
        .add("id='" + id + "'")
        .add("path=" + path)
        .add("dateiTyp='" + dateiTyp + "'")
        .add("dateiName='" + dateiName + "'")
        .add("dateiFormat=" + dateiFormat)
        .add("importEntityData=" + importEntityData)
        .add("error=" + error)
        .add("message='" + message + "'")
        .toString();
  }
}

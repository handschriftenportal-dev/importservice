package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 08.06.2020.
 * @version 1.0
 */
@Entity
public class ImportEntityData {

  @Id
  @JsonIgnore
  private String dbid;

  @JsonProperty("id")
  private String id;

  @JsonProperty("label")
  private String label;

  @JsonProperty("url")
  private URL url;

  protected ImportEntityData() {
    this.dbid = UUID.randomUUID().toString();
  }

  public ImportEntityData(String id, String label, URL url) {
    this.dbid = UUID.randomUUID().toString();
    this.id = id;
    this.label = label;
    this.url = url;
  }

  public String getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  public URL getUrl() {
    return url;
  }

  public String getDbid() {
    return dbid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImportEntityData)) {
      return false;
    }
    ImportEntityData that = (ImportEntityData) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ImportEntityData{" +
        "id='" + id + '\'' +
        ", label='" + label + '\'' +
        '}';
  }
}

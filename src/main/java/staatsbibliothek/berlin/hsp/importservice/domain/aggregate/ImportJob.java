package staatsbibliothek.berlin.hsp.importservice.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.ui.Views;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 29.03.2019.
 * <p>
 * This domain aggregate manages one import job for the Handschriftenportal plattform
 */

@Entity
public class ImportJob {

  @JsonProperty("id")
  @JsonView(Views.List.class)
  @Getter
  @Id
  private String id;

  @JsonProperty("creationDate")
  @JsonView(Views.List.class)
  @Getter
  private LocalDateTime creationDate;

  @JsonProperty("benutzerName")
  @JsonView(Views.List.class)
  @Getter
  private String benutzerName;

  @JsonProperty("importFiles")
  @Getter
  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<ImportFile> importFiles;

  @JsonProperty("name")
  @JsonView(Views.List.class)
  @Getter
  private String name;

  @JsonProperty("importDir")
  @JsonView(Views.List.class)
  @Getter
  private String importDir;

  @JsonProperty("result")
  @JsonView(Views.List.class)
  @Getter
  private TotalResult result;

  @JsonProperty("errorMessage")
  @JsonView(Views.List.class)
  @Getter
  @Column(columnDefinition = "TEXT")
  private String errorMessage;

  @JsonProperty("datatype")
  @JsonView(Views.List.class)
  @Getter
  private String datatype;


  protected ImportJob() {
    //Needed for JPA , Against the immutability ...
  }

  public ImportJob(final String importDir, String name, String benutzerName) {
    this.id = UUID.randomUUID().toString();
    this.creationDate = LocalDateTime.now();
    this.benutzerName = benutzerName;
    this.importFiles = new LinkedHashSet<>();
    this.name = name;
    this.importDir = importDir;
  }

  public ImportJob(final String id, final String importDir, String name, String benutzerName) {
    this.id = id;
    this.creationDate = LocalDateTime.now();
    this.benutzerName = benutzerName;
    this.importFiles = new LinkedHashSet<>();
    this.name = name;
    this.importDir = importDir;
    this.result = TotalResult.NO_RESULT;
  }

  public ImportJob(final String importDir, String name, String benutzerName,
      String datatype, TotalResult result, String errorMessage) {
    this.id = UUID.randomUUID().toString();
    this.creationDate = LocalDateTime.now();
    this.benutzerName = benutzerName;
    this.importFiles = new LinkedHashSet<>();
    this.name = name;
    this.importDir = importDir;
    this.result = result;
    this.errorMessage = errorMessage;
    this.datatype = datatype;
  }

  public static ImportJob copy(final ImportJob job) {

    ImportJob copy = new ImportJob(job.getImportDir(), job.getName(), job.getBenutzerName());
    copy.getImportFiles().addAll(job.getImportFiles());

    return copy;
  }

  void setId(String id) {
    this.id = id;
  }

  void setCreationDate(LocalDateTime creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ImportJob)) {
      return false;
    }
    ImportJob importJob = (ImportJob) o;
    return Objects.equals(id, importJob.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ImportJob{" +
        "id='" + id + '\'' +
        ", creationDate=" + creationDate +
        ", benutzerName='" + benutzerName + '\'' +
        ", name='" + name + '\'' +
        ", importDir='" + importDir + '\'' +
        ", result=" + result +
        ", errorMessage='" + errorMessage + '\'' +
        ", datatype='" + datatype + '\'' +
        '}';
  }

  public void setResult(TotalResult result) {
    this.result = result;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}

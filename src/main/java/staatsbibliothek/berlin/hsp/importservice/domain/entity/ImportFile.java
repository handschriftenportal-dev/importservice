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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 28.03.2019.
 */
@Entity
@Data
public class ImportFile {

  @Id
  private String id;

  @Convert(converter = PathConverter.class)
  private Path path;

  private String dateiTyp;

  private String dateiName;

  private XMLFormate dateiFormat;

  @JsonProperty("importEntityData")
  @Getter
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private Set<ImportEntityData> importEntityData;

  private boolean error;

  @Column(length = 4092)
  private String message;

  protected ImportFile() {
    //Needed for JPA , Against the immutability ...
    importEntityData = new LinkedHashSet<>();
  }

  public ImportFile(String id, String dateiName, Path path, String dateiTyp, XMLFormate dateiFormat,
      boolean error, String message) {
    this.id = id;
    this.dateiName = dateiName;
    this.path = path;
    this.dateiTyp = dateiTyp;
    this.dateiFormat = dateiFormat;
    this.error = error;
    this.message = message;
    this.importEntityData = new LinkedHashSet<>();
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

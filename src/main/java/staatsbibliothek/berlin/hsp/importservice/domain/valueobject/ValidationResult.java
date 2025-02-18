package staatsbibliothek.berlin.hsp.importservice.domain.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph Marten on 21.04.2021 at 08:54
 */
public class ValidationResult implements Serializable {

  private static final long serialVersionUID = -7381765780499522417L;

  boolean isValid;

  String line;

  String column;

  String message;

  private List<ValidationDetails> details = new ArrayList<>();

  public ValidationResult() {
  }

  public ValidationResult(boolean isValid, String line, String column, String message) {
    this.isValid = isValid;
    this.line = line;
    this.column = column;
    this.message = message;
  }

  public boolean isValid() {
    return isValid;
  }

  public void setValid(boolean valid) {
    isValid = valid;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getColumn() {
    return column;
  }

  public void setColumn(String column) {
    this.column = column;
  }

  public ValidationDetails addDetail(String xpath, String error) {
    final ValidationDetails validationDetails = new ValidationDetails(xpath, error);
    this.details.add(validationDetails);
    return validationDetails;
  }

  public List<ValidationDetails> getDetails() {
    return details;
  }

  @Override
  public String toString() {
    return String.format(
        "ValidationResult{isValid=%s, line='%s', column='%s', message='%s',details='%s'}",
        isValid, line, column, message, details.stream().map(ValidationDetails::toString).collect(
            Collectors.joining()));
  }

  public class ValidationDetails {

    private String xpath;
    private String error;
    private List<Diagnostic> diagnostics = new ArrayList<>();

    public ValidationDetails(String xpath, String error) {
      this.xpath = xpath;
      this.error = error;
    }

    public String getXpath() {
      return xpath;
    }

    public String getError() {
      return error;
    }

    public void appendDiagnostics(String lang, String error) {
      this.diagnostics.add(new Diagnostic(lang, error));
    }

    public List<Diagnostic> getDiagnostics() {
      return this.diagnostics;
    }


    @JsonIgnore
    public String getMessageByLanguageCode(String lang) {
      if (diagnostics == null || diagnostics.isEmpty()) {
        return "";
      }

      return this.diagnostics.stream().filter(d -> lang.equals(d.languageCode)).findFirst()
          .map(Diagnostic::getMessage).orElseGet(() -> "");
    }

    @Override
    public String toString() {
      final StringBuilder sb = new StringBuilder("ValidationDetails{");
      sb.append("xpath='").append(xpath).append('\'');
      sb.append(", error='").append(error).append('\'');
      sb.append(", errorTranslations=")
          .append(diagnostics.stream().map(Diagnostic::getMessage).collect(
              Collectors.joining()));
      sb.append('}');
      return sb.toString();
    }
  }

  public class Diagnostic {

    private String languageCode;
    private String message;

    public Diagnostic(String languageCode, String message) {
      this.languageCode = languageCode;
      this.message = message;
    }

    public String getLanguageCode() {
      return languageCode;
    }

    public String getMessage() {
      return message;
    }
  }
}

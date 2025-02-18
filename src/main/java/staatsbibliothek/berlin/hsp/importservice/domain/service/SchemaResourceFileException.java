package staatsbibliothek.berlin.hsp.importservice.domain.service;


/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 02.09.22
 */

public class SchemaResourceFileException extends Exception {

  private static final long serialVersionUID = 3311443109954969313L;

  public SchemaResourceFileException(String message) {
    super(message);
  }

  public SchemaResourceFileException(String message, Throwable cause) {
    super(message, cause);
  }
}

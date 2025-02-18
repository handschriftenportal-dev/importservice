package staatsbibliothek.berlin.hsp.importservice.domain.service;


/**
 * @author michael.hintersonnleitner@sbb.spk-berlin.de
 * @since 02.09.22
 */

public class SchemaResourceFileRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 3311443109954969313L;

  public SchemaResourceFileRuntimeException(String message) {
    super(message);
  }

  public SchemaResourceFileRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}

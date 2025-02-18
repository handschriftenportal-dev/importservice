package staatsbibliothek.berlin.hsp.importservice.domain.exception;

public class ApplicationException extends RuntimeException {

  private static final long serialVersionUID = 7963435337048046594L;

  public ApplicationException(final String message) {
    super(message);
  }

  public ApplicationException(final String message, final Exception exception) {
    super(message, exception);
  }

}

package staatsbibliothek.berlin.hsp.importservice.domain.entity;

public enum TotalResult {

  SUCCESS,
  IN_PROGRESS,
  FAILED,
  NO_RESULT;

  @Override
  public String toString() {

    switch (this) {
      case SUCCESS:
        return "Erfolgreich";
      case FAILED:
        return "Gescheitert";
      case NO_RESULT:
        return "Kein Ergebnis";
      case IN_PROGRESS:
        return "In Bearbeitung";
      default:
        return "";
    }

  }

}

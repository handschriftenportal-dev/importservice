/**
 * Domain Entity for XML Import Upload Job Description
 * Test
 */

export default class ImportJob {
  constructor(id, datum, benutzer, dateianzahl, dateiname, ergebnis) {
    this.id = id;
    this.datum = datum;
    this.benutzer = benutzer;
    this.dateianzahl = dateianzahl;
    this.dateiname = dateiname;
    this.ergebnis = ergebnis;
  }
}

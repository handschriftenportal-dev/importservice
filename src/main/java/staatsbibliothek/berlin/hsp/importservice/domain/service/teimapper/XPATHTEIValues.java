package staatsbibliothek.berlin.hsp.importservice.domain.service.teimapper;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 15.11.2019.
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "tei")
public class XPATHTEIValues {

  private String kodId;
  private String kodDokumentTyp;
  private String kodKulturObjektTyp;
  private String kodGenerierungsDate;

  private String beschreibungid;
  private String beschreibungReferenzes;
  private String beschreibungTitel;
  private String beschreibungDocuments;
  private String beschreibungsGrundsprachen;
  private String beschreibungsGrundsprachenOthers;
  private String beschreibungsAenderungsDatum;
  private String beschreibungsSprache;
  private String beschreibungsBeteiligte;
  private String beschreibungsRechte;
  private String beschreibungsFreitextAll;
  private String beschreibungsKulturobjektTyp;

  private String beschreibungIdentifikationIdent;
  private String beschreibungIdentifikationCollection;
  private String beschreibungIdentifikationCollectionName;

  private String beschreibungdokumentAeusseres;
  private String beschreibungdokumentAeusseresAbemessungHoehe;
  private String beschreibungdokumentAeusseresAbemessungBreite;
  private String beschreibungdokumentAeusseresAbemessungTiefe;
  private String beschreibungdokumentAeusseresAbemessungMasseinheit;
  private String beschreibungdokumentAeusseresAbemessungMaterialBase;
  private String beschreibungdokumentAeusseresAbemessungMaterial;
  private String beschreibungdokumentAeusseresAbemessungUmfangName;
  private String beschreibungdokumentAeusseresSpaltenzahl;
  private String beschreibungdokumentAeusseresZeilenzahl;
  private String beschreibungdokumentAeusseresFormat;

  private String beschreibungsKomponenteGeschichteEntstehungsort;
  private String beschreibungsKomponenteGeschichteEntstehungszeit;
  private String beschreibungsKomponenteGeschichteEntstehungszeitNotBefore;
  private String beschreibungsKomponenteGeschichteEntstehungszeitNotAfter;
  private String beschreibungsKomponenteGeschichteEntstehungszeitPeriod;
  private String beschreibungsKomponenteGeschichteEntstehungszeitFrom;
  private String beschreibungsKomponenteGeschichteEntstehungszeitTo;
  private String beschreibungsKomponenteGeschichteEntstehungszeitWhen;

  private String beschreibungBesitzendenEinrichtungBase;
  private String beschreibungBesitzendenEinrichtungOrt;
  private String beschreibungBesitzendenEinrichtungName;

}

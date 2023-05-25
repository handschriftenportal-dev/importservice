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

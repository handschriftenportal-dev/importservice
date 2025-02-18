package staatsbibliothek.berlin.hsp.importservice.domain;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 28.03.2019.
 */
public interface DateiImportBoundary {

  ImportJob importDateien(ActivityStreamObject activityStreamObject, String fileName, String benutzerName, ActivityStreamsDokumentTyp activityStreamsDokumentTyp) throws ActivityStreamsException;
}

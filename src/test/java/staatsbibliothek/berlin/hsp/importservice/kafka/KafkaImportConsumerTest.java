package staatsbibliothek.berlin.hsp.importservice.kafka;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import staatsbibliothek.berlin.hsp.importservice.domain.service.ImportMessageService;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 04.06.2020.
 * @version 1.0
 */

@SpringBootTest
public class KafkaImportConsumerTest {

  private KafkaImportConsumer kafkaImportConsumer;

  @MockBean
  private ImportMessageService importMessageService;

  @Test
  void testConsume() throws ActivityStreamsException {

    kafkaImportConsumer = new KafkaImportConsumer(importMessageService);

    ActivityStreamObject activityStreamObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.KOD)
        .withUrl("http://localhost")
        .withId("1")
        .withGroupId("beschreibung1")
        .withContent("Test".getBytes())
        .build();

    ActivityStream message = ActivityStream
        .builder()
        .withId(UUID.randomUUID().toString())
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .addObject(activityStreamObject)
        .build();

    kafkaImportConsumer.recieveImportMessage((ActivityStreamMessage) message);

    verify(importMessageService, times(1)).handleMessage((ActivityStreamMessage) message);
  }
}

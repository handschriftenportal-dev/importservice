package staatsbibliothek.berlin.hsp.importservice.kafka;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 08.06.2020.
 * @version 1.0
 */
@SpringBootTest
public class KafkaNormdatenProducerTest {

  @Autowired
  private KafkaNormdatenProducer normdatenProducer;

  @Autowired
  private KafkaProducerListener kafkaProducerListener;

  @MockBean
  KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate;

  @Test
  void testCreation() {

    assertNotNull(normdatenProducer);

    assertNotNull(normdatenProducer.getNormdatenTopic());

    assertNotNull(normdatenProducer.getKafkaTemplate());
  }

  @Test
  void testSending() throws ActivityStreamsException {
    ActivityStream message = ActivityStream
        .builder()
        .withId("123")
        .withType(ActivityStreamAction.ADD)
        .withPublished(LocalDateTime.now())
        .withActorName("Konrad Eichstädt")
        .withTargetName("Ortsregister")
        .build();

    KafkaNormdatenProducer producer = new KafkaNormdatenProducer("test", kafkaTemplate, kafkaProducerListener);

    producer.sendMessage(message);

    verify(kafkaTemplate, times(1)).send((Message<ActivityStream>) any());
  }
}

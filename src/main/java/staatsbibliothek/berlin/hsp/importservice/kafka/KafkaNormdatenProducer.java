package staatsbibliothek.berlin.hsp.importservice.kafka;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 08.06.2020.
 * @version 1.0
 */
@Slf4j
@Component
public class KafkaNormdatenProducer {

  String normdatenTopic;

  KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate;

  KafkaProducerListener kafkaProducerListener;

  public KafkaNormdatenProducer(@Value("${normdaten.topic}") String normdatenTopic,
      KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate,
      KafkaProducerListener kafkaProducerListener) {
    this.normdatenTopic = normdatenTopic;
    this.kafkaTemplate = kafkaTemplate;
    this.kafkaProducerListener = kafkaProducerListener;
  }

  public void sendMessage(ActivityStream activityStreamMessage) {

    log.info("Sending message to normdaten with ID {} and Action {} ",
        activityStreamMessage.getId(),
        activityStreamMessage.getAction());

    Message<ActivityStream> message = MessageBuilder
        .withPayload(activityStreamMessage)
        .setHeader(KafkaHeaders.TOPIC, normdatenTopic)
        .setHeader(KafkaHeaders.KEY, activityStreamMessage.getId())
        .build();

    kafkaTemplate.setProducerListener(kafkaProducerListener);
    kafkaTemplate.send(message);

  }

  public String getNormdatenTopic() {
    return normdatenTopic;
  }

  public KafkaTemplate<String, ActivityStreamMessage> getKafkaTemplate() {
    return kafkaTemplate;
  }
}

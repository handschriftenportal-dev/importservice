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

    log.info("Sending message to normdaten with ID {} and Action {} ", activityStreamMessage.getId(),
        activityStreamMessage.getAction());

    Message<ActivityStream> message = MessageBuilder
        .withPayload(activityStreamMessage)
        .setHeader(KafkaHeaders.TOPIC, normdatenTopic)
        .setHeader(KafkaHeaders.MESSAGE_KEY, activityStreamMessage.getId())
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

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

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamBuilder;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectBuilder;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObjectTag;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.service.ImportJobConvert;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@Slf4j
@Component
public class KafkaNachweisProducer {

  @Value("${import.topic}")
  String importTopic;

  @Value("${import.serverurl}")
  String importServerUrl;

  @Autowired
  KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate;
  @Autowired
  KafkaProducerListener kafkaProducerListener;
  @Autowired
  ImportJobConvert importJobConvert;
  @Autowired
  private MessageSource messageSource;

  public void sendMessage(ImportJob job, Optional<ActivityStreamObjectTag> internExternTag) {
    try {
      sendMessage(importTopic, job.getId(), job, internExternTag);
    } catch (Exception e) {
      log.error("Problem during sending job {}", e.getMessage(), e);
    }
  }

  public void sendMessageWithActivityStream(ActivityStream activityStreamMessage) {

    log.info("Sending message to nachweis with ID {} and Action {} ", activityStreamMessage.getId(),
        activityStreamMessage.getAction());

    Message<ActivityStream> message = MessageBuilder
        .withPayload(activityStreamMessage)
        .setHeader(KafkaHeaders.TOPIC, importTopic)
        .setHeader(KafkaHeaders.MESSAGE_KEY, activityStreamMessage.getId())
        .build();

    kafkaTemplate.setProducerListener(kafkaProducerListener);
    kafkaTemplate.send(message);

  }

  void sendMessage(String topic, String key, ImportJob importJob, Optional<ActivityStreamObjectTag> internExternTag)
      throws ActivityStreamsException, JsonProcessingException {

    String importUrl = importServerUrl + importJob.getId();

    final ActivityStreamBuilder activityStreamBuilder = ActivityStream.builder();
    activityStreamBuilder.withId(key)
        .withType(ActivityStreamAction.ADD)
        .withActorName(importJob.getBenutzerName())
        .withTargetName(importJob.getName());

    for (ImportFile importFile : importJob.getImportFiles()) {
      try {
        Path path = importFile.getPath();
        final byte[] xmlContent = Files.readAllBytes(path);

        ActivityStreamObjectBuilder objectFileContentBuilder = ActivityStreamObject.builder();
        ActivityStreamObject activityStreamObject = objectFileContentBuilder
            .withId(UUID.randomUUID().toString())
            .withCompressed(true)
            .withContent(xmlContent)
            .withName(importFile.getDateiName())
            .withType(ActivityStreamsDokumentTyp.BESCHREIBUNG)
            .withUrl(importUrl)
            .build();

        activityStreamBuilder.addObject(activityStreamObject);

      } catch (Exception e) {
        log.error("Error during send Message to Kafka.", e);
        importFile
            .applyError(messageSource.getMessage("import.error.send.message", null, LocaleContextHolder.getLocale()));
      }
    }

    final List<ActivityStreamObjectTag> tags = new ArrayList<>();
    if (internExternTag.isPresent()) {
      tags.add(internExternTag.get());
    }

    ActivityStreamObject importJobObject = ActivityStreamObject.builder()
        .withId(UUID.randomUUID().toString())
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.IMPORT)
        .withContent(importJobConvert.toJson(importJob))
        .withName(importJob.getName())
        .withMediaType("application/json")
        .withTag(tags)
        .build();
    activityStreamBuilder.addObject(importJobObject);
    final ActivityStream stream = activityStreamBuilder.build();

    Message<ActivityStream> message = MessageBuilder
        .withPayload(stream)
        .setHeader(KafkaHeaders.TOPIC, topic)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        .build();

    kafkaTemplate.setProducerListener(kafkaProducerListener);
    kafkaTemplate.send(message);
  }

  protected void setKafkaTemplate(
      KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

}
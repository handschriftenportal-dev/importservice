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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@Slf4j
@Component
public class KafkaProducerListener implements ProducerListener<String, ActivityStreamMessage> {

  private ImportJobRepository importJobRepository;

  @Autowired
  public KafkaProducerListener(
      ImportJobRepository importJobRepository) {
    this.importJobRepository = importJobRepository;
  }

  @Override
  public void onSuccess(String topic, Integer partition, String key, ActivityStreamMessage value,
      RecordMetadata recordMetadata) {

    log.debug("Sent to topic {} with offset {} partition {} , data {} ",
        recordMetadata.topic(), recordMetadata.offset(),
        recordMetadata.partition(), value);

  }

  @Override
  public void onError(String topic, Integer partition, String key, ActivityStreamMessage value,
      Exception exception) {
    log.error("Unable to send message {} due to {}", value, exception.getMessage());

    value.getObjects().stream().filter(o -> o.getType().equals(ActivityStreamsDokumentTyp.IMPORT)).findFirst()
        .ifPresent(o -> {

          try {

            ImportJob importJob = ObjectMapperFactory.getObjectMapper().readValue(o.getContent(), ImportJob.class);

            importJobRepository.findById(importJob.getId()).ifPresent(j -> {
              j.setErrorMessage(exception.getMessage());
              j.setResult(TotalResult.FAILED);

              importJobRepository.save(j);
            });

          } catch (IOException | ActivityStreamsException e) {
            log.error("Error during Reading ImportJob from Message", e);
          }

        });
  }

}

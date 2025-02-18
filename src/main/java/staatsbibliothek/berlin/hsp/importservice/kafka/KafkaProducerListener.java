package staatsbibliothek.berlin.hsp.importservice.kafka;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.lang.Nullable;
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
  public void onSuccess(ProducerRecord<String, ActivityStreamMessage> producerRecord,
      RecordMetadata recordMetadata) {

    log.debug("Sent to topic {} with offset {} partition {} , data {} ",
        recordMetadata.topic(), recordMetadata.offset(),
        recordMetadata.partition(), producerRecord.value());

  }

  @Override
  public void onError(ProducerRecord<String, ActivityStreamMessage> producerRecord,
      @Nullable RecordMetadata recordMetadata, Exception exception) {
    log.error("Unable to send message {} due to {}", producerRecord.value(),
        exception.getMessage());

    producerRecord.value().getObjects().stream()
        .filter(o -> o.getType().equals(ActivityStreamsDokumentTyp.IMPORT))
        .findFirst()
        .ifPresent(o -> {

          try {

            ImportJob importJob = ObjectMapperFactory.getObjectMapper()
                .readValue(o.getContent(), ImportJob.class);

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

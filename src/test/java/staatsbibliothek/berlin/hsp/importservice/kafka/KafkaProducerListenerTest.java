package staatsbibliothek.berlin.hsp.importservice.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamBuilder;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.TotalResult;
import staatsbibliothek.berlin.hsp.importservice.domain.service.ImportJobConvert;
import staatsbibliothek.berlin.hsp.importservice.persistence.ImportJobRepository;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 10.06.2020.
 * @version 1.0
 */

@SpringBootTest
public class KafkaProducerListenerTest {

  @Autowired
  private KafkaProducerListener listener;

  @Autowired
  private ImportJobRepository jobRepository;

  @Autowired
  private ImportJobConvert importJobConvert;

  @Test
  void testOnError() throws JsonProcessingException, ActivityStreamsException {

    ImportJob importJob = new ImportJob("123", "Test", "Konrad");

    jobRepository.save(importJob);

    ActivityStreamObject importJobObject = ActivityStreamObject.builder()
        .withCompressed(true)
        .withType(ActivityStreamsDokumentTyp.IMPORT)
        .withContent(importJobConvert.toJson(importJob))
        .withName(importJob.getName())
        .withMediaType("application/json")
        .build();

    final ActivityStreamBuilder activityStreamBuilder = ActivityStream.builder();
    activityStreamBuilder.withId("1234")
        .withType(ActivityStreamAction.ADD)
        .addObject(importJobObject)
        .withActorName(importJob.getBenutzerName())
        .withTargetName(importJob.getName());

    final ActivityStreamMessage stream = (ActivityStreamMessage) activityStreamBuilder.build();
    ProducerRecord<String, ActivityStreamMessage> producerRecord = new ProducerRecord("1", stream);
    RecordMetadata recordMetadata = new RecordMetadata(new TopicPartition("data-import", 3), 0, 0,
        0, 0, 0);

    listener.onError(producerRecord, recordMetadata, new Exception("Fehler"));

    jobRepository.findById(importJob.getId()).ifPresentOrElse(j -> {
      assertEquals("Fehler", j.getErrorMessage());
      assertEquals(TotalResult.FAILED, j.getResult());
    }, () -> {
      Assertions.assertTrue(false);
    });
  }
}

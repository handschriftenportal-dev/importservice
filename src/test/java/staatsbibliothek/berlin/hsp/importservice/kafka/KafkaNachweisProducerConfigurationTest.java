package staatsbibliothek.berlin.hsp.importservice.kafka;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@SpringBootTest
public class KafkaNachweisProducerConfigurationTest {

  @Autowired
  private KafkaProducerConfiguration kafkaProducerConfiguration;

  @Test
  void testKafkaProducerConfigurationCreation() {
    assertNotNull(kafkaProducerConfiguration);
  }

  @Test
  void testproducerFactoryCreation() {
    ProducerFactory<String, ActivityStreamMessage> factory = kafkaProducerConfiguration
        .producerFactory();

    assertNotNull(factory);
  }

  @Test
  void testCreateKafkaTemplate() {
    KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate = kafkaProducerConfiguration
        .kafkaTemplate();

    assertNotNull(kafkaTemplate);
  }

}

package staatsbibliothek.berlin.hsp.importservice.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 04.06.2020.
 * @version 1.0
 */

@SpringBootTest
public class KafkaImportConsumerConfigurationTest {

  @Autowired
  private KafkaImportConsumerConfiguration configuration;

  @Test
  void testCreation() {
    assertNotNull(configuration);
  }

  @Test
  void testconsumerConfigs() {

    Map<String, Object> consumer = configuration.consumerConfigs();

    assertTrue(consumer.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
    assertTrue(consumer.containsKey(ConsumerConfig.GROUP_ID_CONFIG));
    assertTrue(consumer.containsKey(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
    assertTrue(consumer.containsKey(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
  }

  @Test
  void testconsumerFactory() {
    assertNotNull(configuration.consumerFactory());

    assertEquals(configuration.consumerConfigs(), configuration.consumerFactory().getConfigurationProperties());
  }

  @Test
  void testkafkaListenerContainerFactory() {
    assertNotNull(configuration.kafkaListenerContainerFactory());
  }
}

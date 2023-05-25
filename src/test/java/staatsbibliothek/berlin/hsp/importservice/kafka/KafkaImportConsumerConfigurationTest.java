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

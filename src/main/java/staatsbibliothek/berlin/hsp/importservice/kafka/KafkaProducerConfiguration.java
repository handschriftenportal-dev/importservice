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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@Configuration
public class KafkaProducerConfiguration {

  public KafkaProducerConfiguration(
      @Value("${spring.kafka.bootstrap-servers}") String bootstrapAddress,
      @Autowired ProducerListener<String, ActivityStreamMessage> loggingProducerListener) {
    this.bootstrapAddress = bootstrapAddress;
    this.loggingProducerListener = loggingProducerListener;
  }

  private String bootstrapAddress;

  private ProducerListener<String, ActivityStreamMessage> loggingProducerListener;

  @Bean
  public ProducerFactory<String, ActivityStreamMessage> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        bootstrapAddress);
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class);

    // Setting to  increase the realibility of message producer
    // Doesn't work at the moment , maybe because of only two Broker Cluster

    configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 500000000);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    configProps.put(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
    configProps.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

    // Batch producing at compression to  encrease the throuthput
    // Without Batch sending 1000 TEI XML in 90 Seconds
    // With Batch sending 1000 TEI XML in 120 Seconds :(???
    // Active MQ sending 1000 TEI XML in 4 Minutes

    configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    configProps.put(ProducerConfig.LINGER_MS_CONFIG, "30");
    configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));

    JsonSerializer jsonSerializer = new JsonSerializer(ObjectMapperFactory.getObjectMapper());
    jsonSerializer.configure(configProps, false);

    return new DefaultKafkaProducerFactory<>(configProps,
        new StringSerializer(), jsonSerializer);
  }

  @Bean
  public KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate() {

    KafkaTemplate template = new KafkaTemplate<>(producerFactory());
    template.setProducerListener(loggingProducerListener);

    return template;
  }

}

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
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.ResourceUtils;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.XMLFormate;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 22.03.2019.
 */
@Slf4j
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = false, topics = "test", brokerProperties = {
    "listeners=PLAINTEXT://localhost:3333", "port=3333",
    "transaction.state.log.replication.factor=1", "min.insync.replicas=1",
    "transaction.state.log.min.isr=1", "offsets.topic.replication.factor=1"})
public class KafkaNachweisProducerTest {

  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Autowired
  private KafkaNachweisProducer kafkaNachweisProducer;

  @Autowired
  private KafkaProducerListener kafkaProducerListener;

  @Value("${import.datadirectory}")
  private String dataDirectory;

  @Test
  void testSendMessageWithImportFile() throws Exception {
    KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate = new KafkaTemplate<>(
        producerFactory());
    kafkaTemplate.setDefaultTopic("test");
    kafkaTemplate.setProducerListener(kafkaProducerListener);

    kafkaNachweisProducer.setKafkaTemplate(kafkaTemplate);
    kafkaNachweisProducer.importTopic = "test";

    final Path path = new File(dataDirectory + File.separator + "tei-msDesc_Koch.xml").toPath();
    Files.copy(ResourceUtils.getFile("classpath:" + "tei-msDesc_Koch.xml").toPath(),
        path,
        StandardCopyOption.REPLACE_EXISTING);

    ImportJob job = new ImportJob(dataDirectory, "tei-msDesc_Koch.xml", "test");
    ImportFile importFile = new ImportFile(UUID.randomUUID().toString(), "tei-msDesc_Koch.xml", path,
        null, XMLFormate.UNBEKANNT, false, null);
    job.getImportFiles().add(importFile);

    kafkaNachweisProducer.sendMessage(job, Optional.empty());

    Assertions.assertFalse(importFile.isError());
  }

  @Test
  void testSendMessage() throws Exception {
    Consumer<String, ActivityStreamMessage> consumer = createKafkaTestCosumer();

    KafkaTemplate<String, ActivityStreamMessage> kafkaTemplate = new KafkaTemplate<>(
        producerFactory());
    kafkaTemplate.setDefaultTopic("test");
    kafkaTemplate.setProducerListener(kafkaProducerListener);
    ImportJob job = new ImportJob(dataDirectory, "tei-msDesc_Koch.xml", "test");
    ImportFile importFile = new ImportFile(UUID.randomUUID().toString(), "tei-msDesc_Koch.xml", null,
        null, XMLFormate.UNBEKANNT, false, null);

    this.embeddedKafka.consumeFromEmbeddedTopics(consumer, "test");

    kafkaNachweisProducer.setKafkaTemplate(kafkaTemplate);

    kafkaNachweisProducer.sendMessage("test", UUID.randomUUID().toString(), job, Optional.empty());

    log.info("Consumed all records for test");

    ConsumerRecords<String, ActivityStreamMessage> replies = KafkaTestUtils
        .getRecords(consumer, 2000);

    log.info("Consuming Testrecords {} ", replies.count());

    Assertions.assertEquals(replies.count(), 1);

    embeddedKafka.destroy();
  }

  private Consumer<String, ActivityStreamMessage> createKafkaTestCosumer() {
    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("hsp", "true", this.embeddedKafka);

    consumerProps.put(
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
        StringDeserializer.class);
    consumerProps.put(
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        JsonDeserializer.class);
    consumerProps.put(
        JsonDeserializer.TRUSTED_PACKAGES,
        "de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model");

    JsonDeserializer jsonDeserializer = new JsonDeserializer<>(ObjectMapperFactory.getObjectMapper());
    jsonDeserializer.configure(consumerProps, false);

    ConsumerFactory<String, ActivityStreamMessage> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps, new StringDeserializer(), jsonDeserializer);

    return cf.createConsumer();
  }

  private ProducerFactory<String, ActivityStreamMessage> producerFactory() {

    Map<String, Object> configProps = KafkaTestUtils.producerProps(embeddedKafka);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
    configProps.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        StringSerializer.class);
    configProps.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        JsonSerializer.class);
    configProps.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 50000000);
    configProps.put("retries", 1);

    DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory<>(configProps,
        new StringSerializer(), new JsonSerializer<>(ObjectMapperFactory.getObjectMapper()));

    return factory;
  }
}
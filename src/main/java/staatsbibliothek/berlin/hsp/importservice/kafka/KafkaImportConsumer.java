package staatsbibliothek.berlin.hsp.importservice.kafka;

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.service.ImportMessageService;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 03.06.2020.
 * @version 1.0
 * <p>
 * This class is in charge for recieving messages from nachweis witch contain import xml data.
 */

@Component
@Scope("singleton")
public class KafkaImportConsumer {

  @Autowired
  public KafkaImportConsumer(ImportMessageService messageService) {
    this.messageService = messageService;
  }

  private final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

  private final ImportMessageService messageService;

  @KafkaListener(topics = "${dataimport.topic}", clientIdPrefix = "import" , concurrency = "${listen.concurrency:1}")
  public void recieveImportMessage(ActivityStreamMessage data) {

    logger.info("Recieving kafka data import message: {} ", data.getId());

    messageService.handleMessage(data);

  }
}

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

import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.service.KafkaImportMessageService;

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
  public KafkaImportConsumer(KafkaImportMessageService messageService) {
    this.messageService = messageService;
  }

  private Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

  private KafkaImportMessageService messageService;

  @KafkaListener(topics = "${dataimport.topic}", clientIdPrefix = "import" , concurrency = "${listen.concurrency:1}")
  public void recieveImportMessage(ActivityStreamMessage data) {

    logger.info("Recieving kafka data import message: {} ", data.getId());

    messageService.handleMessage(data);

  }
}

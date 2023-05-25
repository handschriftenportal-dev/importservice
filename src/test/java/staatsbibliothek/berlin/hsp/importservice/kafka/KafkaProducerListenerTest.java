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

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.exceptions.ActivityStreamsException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStream;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamBuilder;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.ActivityStreamObject;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamAction;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.api.model.enums.ActivityStreamsDokumentTyp;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.model.ActivityStreamMessage;
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

    listener.onError("test", 1, "1234", stream, new Exception("Fehler"));

    jobRepository.findById(importJob.getId()).ifPresentOrElse(j -> {
      assertEquals("Fehler", j.getErrorMessage());
      assertEquals(TotalResult.FAILED, j.getResult());
    }, () -> {
      Assertions.assertTrue(false);
    });
  }
}

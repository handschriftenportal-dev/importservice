package staatsbibliothek.berlin.hsp.importservice.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.staatsbibliothek.berlin.hsp.messaging.activitystreams.impl.mapper.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 12.06.2020.
 * @version 1.0
 */

@Component
@Slf4j
public class ImportJobConvert {

  public String toJson(ImportJob job) throws JsonProcessingException {
    return ObjectMapperFactory.getObjectMapper().writeValueAsString(job);
  }
}

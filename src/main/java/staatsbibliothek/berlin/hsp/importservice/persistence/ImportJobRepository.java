package staatsbibliothek.berlin.hsp.importservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 29.03.2019.
 */

@Repository
public interface ImportJobRepository extends CrudRepository<ImportJob, String> {

}

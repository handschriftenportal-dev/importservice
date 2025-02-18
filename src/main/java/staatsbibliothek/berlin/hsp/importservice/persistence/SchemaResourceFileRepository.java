package staatsbibliothek.berlin.hsp.importservice.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;

@Repository
public interface SchemaResourceFileRepository extends CrudRepository<SchemaResourceFile, String> {

}

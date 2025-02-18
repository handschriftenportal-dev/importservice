package staatsbibliothek.berlin.hsp.importservice.domain.service;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.validation.ReportEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import staatsbibliothek.berlin.hsp.importservice.domain.SchemaResourceFileBoundary;
import staatsbibliothek.berlin.hsp.importservice.domain.aggregate.ImportJob;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.ImportFile;
import staatsbibliothek.berlin.hsp.importservice.domain.entity.SchemaResourceFile;
import staatsbibliothek.berlin.hsp.importservice.domain.valueobject.DateiFormate;

/**
 * @author konrad.eichstaedt@sbb.spk-berlin.de on 12.01.24.
 * @version 1.0
 */
@Component
@Slf4j
public class RDFService {

  public static final String HSP_DATATYP_KONZEPT = "KONZEPT";
  private final SchemaResourceFileBoundary schemaResourceFileBoundary;

  @Autowired
  public RDFService(SchemaResourceFileBoundary schemaResourceFileBoundary) {
    this.schemaResourceFileBoundary = schemaResourceFileBoundary;
  }

  public void applyRDFFormat(ImportFile importFile) {

    if (importFile == null || importFile.getPath() == null) {
      throw new IllegalArgumentException("Error missing importfile");
    }

    Lang lang = RDFLanguages.filenameToLang(importFile.getPath().toUri().toString());

    log.info("RDF language {} ", lang);

    if (isTurtle(lang)) {
      importFile.setDateiFormat(DateiFormate.RDF_TURTLE);
    } else {
      importFile.setDateiFormat(DateiFormate.UNBEKANNT);
    }
  }

  public static boolean isTurtle(Lang lang) {
    return Lang.TURTLE.equals(lang);
  }

  public void validateSchema(ImportFile importFile, ImportJob importJob) {

    try {

      if (!DateiFormate.RDF_TURTLE.equals(importFile.getDateiFormat())) {
        throw new IllegalArgumentException(
            "Wrong RDF File Type for validation" + importFile.getDateiFormat());
      }

      if (HSP_DATATYP_KONZEPT.equals(importJob.getDatatype())) {

        log.info("Validate RDF against SHACL {} ", importFile);

        Graph shapesGraph = RDFDataMgr.loadGraph(loadSHACLSchema().toString());
        Graph dataGraph = RDFDataMgr.loadGraph(importFile.getPath().toUri().toString());

        Shapes shapes = Shapes.parse(shapesGraph);

        ValidationReport report = ShaclValidator.get().validate(shapes, dataGraph);

        importFile.setError(report.conforms());
        if (importFile.isError()) {
          log.error("Error during SHACL Validation {}", report.getEntries());
          importFile.setMessage(report.getEntries().stream().map(ReportEntry::message)
              .collect(Collectors.joining(";")));
        }
      }

    } catch (Exception error) {
      log.error("Error during Schema SHACL Validation {}", error.getMessage(), error);
      importFile.setError(true);
      importFile.setMessage(error.getMessage());
    }
  }

  URI loadSHACLSchema() throws Exception {
    Optional<SchemaResourceFile> schemaResourceFile = schemaResourceFileBoundary.findOptionalById(
        SchemaResourceFileBoundary.ID_RDF_TURTLE);
    Optional<Resource> resource = schemaResourceFile.flatMap(SchemaResourceFile::getDatei);

    if (resource.isPresent()) {
      return resource.get().getURI();
    }
    throw new IllegalStateException("SHACL Schema not found");
  }
}

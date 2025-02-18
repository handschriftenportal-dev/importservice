package staatsbibliothek.berlin.hsp.importservice.domain.entity;

import java.nio.file.Path;
import java.nio.file.Paths;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PathConverter implements AttributeConverter<Path, String> {

  @Override
  public String convertToDatabaseColumn(Path attribute) {
    return attribute == null ? null : attribute.toString();
  }

  @Override
  public Path convertToEntityAttribute(String dbData) {
    return dbData == null ? null : Paths.get(dbData);
  }

}
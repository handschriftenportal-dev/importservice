package staatsbibliothek.berlin.hsp.importservice.ui;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */

@Configuration
@EnableAutoConfiguration
public class SwaggerConfig {

  public static final String TAG_BESCHREIBUNG = "Beschreibung";
  public static final String TAG_DIGITALISAT = "Digitalisat";
  public static final String TAG_KOD = "KulturObjektDokument";
  public static final String TAG_KATALOG = "Katalog";
  public static final String TAG_NORMDATEN = "Normdaten";
  public static final String TAG_IMPORTJOB = "ImportJob";
  public static final String TAG_SCHEMARESOURCEFILE = "SchemaPflegeDatei";
  public static final String TAG_TEI_XML = "TEI-XML";


  @Bean
  public OpenAPI importAPIDescription() {
    return new OpenAPI().servers(List.of(new Server().url("/").description("Server Default URL")))
        .info(new Info().title("Handschriftenportal Importservice REST Interface").description(
                "Handschriftenportal REST Interface for Handling XML Dataimport.").version("1.0.42")
            .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"))
            .contact(new Contact().name("Staatsbibliothek zu Berlin - Handschriftenportal")
                .url("https://handschriftenportal.de/")));
  }

}

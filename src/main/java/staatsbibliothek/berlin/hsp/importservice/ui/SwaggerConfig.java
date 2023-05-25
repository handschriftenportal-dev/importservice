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

package staatsbibliothek.berlin.hsp.importservice.ui;

import io.swagger.annotations.Api;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by konrad.eichstaedt@sbb.spk-berlin.de on 05.04.2019.
 */
@Configuration
@EnableSwagger2
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
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
        .paths(PathSelectors.regex("/rest/.*"))
        .build()
        .apiInfo(apiInfo())
        .tags(
            new Tag(TAG_BESCHREIBUNG, "Operations related to descriptions."),
            new Tag(TAG_DIGITALISAT, "Operations related to digital surrogates."),
            new Tag(TAG_KOD, "Operations related to cultureObjectDocuments."),
            new Tag(TAG_KATALOG, "Operations related to catalogs."),
            new Tag(TAG_NORMDATEN, "Operations related to metadata."),
            new Tag(TAG_IMPORTJOB, "Operations related to import-jobs."),
            new Tag(TAG_SCHEMARESOURCEFILE, "Operations related to schema-resource-files."),
            new Tag(TAG_TEI_XML, "Operations related to tei-xml.")
        )
        .directModelSubstitute(Path.class, String.class)
        .directModelSubstitute(URL.class, String.class);
  }

  ApiInfo apiInfo() {
    return new ApiInfo(
        "Handschriftenportal Importservice REST Interface",
        "Handschriftenportal REST Interface for Handling XML Dataimport.",
        "1.0.42",
        "a url to terms and services",
        new Contact("Staatsbibliothek zu Berlin - Handschriftenportal -",
            "https://handschriftenportal.de/",
            "robert.giel@sbb.spk-berlin.de"),
        "MIT License",
        "https://opensource.org/licenses/MIT", Arrays.asList());
  }

}

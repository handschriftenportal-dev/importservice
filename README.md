# Handschriftenportal - Import for Manuscript Description -

**Description**:  This module offers a REST API Microservice which makes it possible to validate or import XML Data into the manuscripts portal system.
The uploaded data will be distributed by import service using Apache Kafka Topics. The main purpose of this module is to react as data gateway for 
manuscript data. This software module is part of the administration unit of the manuscripts portal "[Handschriftenportal](https://handschriftenportal.de/)".

- **Technology stack**: The service is implemented using Java Programming language. The Application Server is arranged by using the Spring Boot Framework. 
- **Status**:  Beta (in development)
- **Links:**
- Describe what sets this apart from related-projects. Linking to another doc or page is OK if this can't be expressed
  in a sentence or two.

## Getting started

1. Get the source code

   ```
   git clone https://github.com/handschriftenportal-dev/importservice
   ```

2. Start the server

   ```java
   mvn clean install spring-boot:run -Dspring-boot.run.profiles=dev-linux -Dmaven.skipTests
   ```

3. Open the editor within the browser

   ```
   http://b-dev1047.pk.de:9296/swagger-ui.html
   ```

Afterwards you can start using the REST API. The Service mainly works only in combination with Apache Kafka. 

## Configuration

The configuration of the service is implemented using resources properties files. 

## Usage

## How to test the software

1. To run all unit tests please use the following command

```
mvn clean test
```

## Known issues

## Getting help

To get help please use our contact possibilities on [twitter](https://twitter.com/hsprtl)
and [handschriftenportal.de](https://handschriftenportal.de/)

## Getting involved

To get involed please contact our develoment
team [handschriftenportal@sbb.spk-berlin.de](handschriftenportal-dev@sbb.spk-berlin.de)

## Open source licensing info

The project is published under the [MIT License](https://opensource.org/licenses/MIT).

## Credits and references

1. [Github Project Repository](https://github.com/handschriftenportal-dev)
2. [Project Page](https://handschriftenportal.de/)
3. [Internal Documentation](doc/ARC42.md)

## RELEASE Notes

ARG SOURCE_IMAGE_TAG
FROM artefakt.dev.sbb.berlin:5000/sbb/base-images/java-base:$SOURCE_IMAGE_TAG

WORKDIR /app

COPY ./target/*.war importservice.war

RUN addgroup --system importservice && adduser --system --shell /bin/false --ingroup importservice importservice
RUN chown -R importservice:importservice /app
USER importservice
ENTRYPOINT ["java","-jar", "/app/importservice.war"]
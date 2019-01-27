# build stage
FROM maven:3.5.4-jdk-11-slim AS build

WORKDIR /build
COPY . .

ARG revision=DEV
ARG storageContainerName=gongyu

RUN mvn -Drevision=$revision -Dgongyu.storageContainerName=$storageContainerName clean package

# release stage
FROM azul/zulu-openjdk-alpine:11

MAINTAINER Martin Pešek <martin@orbu.net>

ARG revision

COPY --from=build /build/target/gongyu-$revision.war ./gongyu.war

ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xmx128m", "-Xms32m", "-jar", "/gongyu.war"]

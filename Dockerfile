# build stage
FROM maven:3.5.4-jdk-11-slim AS build

WORKDIR /build
COPY . .

ARG revision=DEV
ARG storageContainerName=gongyu

RUN mvn -Drevision=$revision -Dgongyu.storageContainerName=$storageContainerName clean package

# release stage
FROM openjdk:11-slim

MAINTAINER Martin Pešek <martin@orbu.net>

ARG revision

COPY --from=build /build/target/gongyu-$revision.war ./gongyu.war

ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAM=92m", "-jar", "/gongyu.war"]

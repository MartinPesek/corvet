# build stage
FROM maven:3.5.3-jdk-10-slim AS build

WORKDIR /build
COPY . .

ARG revision=DEV
ARG storageContainerName=gongyu

RUN mvn -Drevision=$revision -Dgongyu.storageContainerName=$storageContainerName clean package

# release stage
FROM openjdk:10-slim

MAINTAINER Martin Pešek <pesek.dev@gmail.com>

ARG revision

COPY --from=build /build/target/gongyu-$revision.war ./gongyu.war

ENTRYPOINT ["java", "-jar", "/gongyu.war"]

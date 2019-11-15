# build stage
FROM maven:3.5.4-jdk-11-slim AS build

WORKDIR /build
COPY . .

ARG revision=DEV
ARG storageContainerName=gongyu
ARG serverName=local
ARG sentryDsn

RUN mvn -Drevision=$revision -Dgongyu.storageContainerName=$storageContainerName -Dserver.name=$serverName -Dsentry.dsn=$sentryDsn clean package

# release stage
FROM adoptopenjdk/openjdk11-openj9:latest

ARG revision

COPY --from=build /build/target/gongyu-$revision.war ./corvet.war

ENTRYPOINT ["java", "-Xss256k", "-jar", "/corvet.war"]

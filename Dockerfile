# build stage
FROM maven:3.5.4-jdk-11-slim AS build

WORKDIR /build
COPY . .

RUN mvn clean package

# release stage
FROM openjdk:11-slim

COPY --from=build /build/web/target/web-0.0.1-SNAPSHOT.jar ./corvet.jar

ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "/corvet.jar"]

FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY kafka-events ./kafka-events

WORKDIR /build/kafka-events
RUN mvn clean install -DskipTests

WORKDIR /build
COPY proto ./proto

WORKDIR /build/patient-service
COPY patient-service/pom.xml .
COPY patient-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder \
    /build/patient-service/target/patient-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4000
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
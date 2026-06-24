FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY kafka-events ./kafka-events

WORKDIR /build/kafka-events
RUN mvn clean install -DskipTests

WORKDIR /build/inventory-service

COPY inventory-service/pom.xml .
COPY inventory-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder \
    /build/inventory-service/target/inventory-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4011

ENTRYPOINT ["java","-jar","app.jar"]
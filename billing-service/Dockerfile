FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Install shared library first
COPY kafka-events ./kafka-events

WORKDIR /build/kafka-events

RUN mvn clean install -DskipTests

WORKDIR /build
COPY proto ./proto

WORKDIR /build/billing-service

COPY billing-service/pom.xml .
COPY billing-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder \
    /build/billing-service/target/billing-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4001
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
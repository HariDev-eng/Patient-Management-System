FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build


COPY proto ./proto

WORKDIR /build/proto
RUN mvn clean install -DskipTests

WORKDIR /build

COPY nurse-service ./nurse-service

WORKDIR /build/nurse-service

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /build/nurse-service/target/nurse-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4008

ENTRYPOINT ["java","-jar","app.jar"]
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY proto ./proto

WORKDIR /build/proto
RUN mvn clean install -DskipTests

WORKDIR /build

COPY prescription-svc ./prescription-svc

WORKDIR /build/prescription-svc

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /build/prescription-svc/target/prescription-svc-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4000
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
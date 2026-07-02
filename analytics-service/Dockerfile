FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY proto ./proto

WORKDIR /workspace/proto
RUN mvn clean install -DskipTests

WORKDIR /workspace

COPY analytics-service ./analytics-service


# Build analytics-service
WORKDIR /workspace/analytics-service

COPY analytics-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY analytics-service/src ./src

RUN mvn clean package -DskipTests

# Runtime image
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder \
    /workspace/analytics-service/target/analytics-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4002

ENTRYPOINT ["java","-jar","app.jar"]
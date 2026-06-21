FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

# Copy shared library
COPY kafka-events ./kafka-events

# Install kafka-events into container's local Maven repo
RUN cd kafka-events && mvn clean install -DskipTests

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
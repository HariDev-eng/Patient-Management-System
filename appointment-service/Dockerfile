FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app/appointment-service

COPY appointment-service/pom.xml .
COPY appointment-service/src ./src

COPY proto ../proto

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/appointment-service/target/appointment-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4007

ENTRYPOINT ["java", "-jar", "app.jar"]
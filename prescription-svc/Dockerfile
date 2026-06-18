FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/prescription-svc-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4000
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
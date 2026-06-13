FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY patient-service/pom.xml .
COPY patient-service/src ./src
COPY proto ./proto
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/patient-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4000
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
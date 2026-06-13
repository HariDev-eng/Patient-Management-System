FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app/billing-service

COPY billing-service/pom.xml .
COPY billing-service/src ./src

COPY proto ../proto

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder \
    /app/billing-service/target/billing-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4001
EXPOSE 9001

ENTRYPOINT ["java","-jar","app.jar"]
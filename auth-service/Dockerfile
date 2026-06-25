FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY auth-service/pom.xml .

RUN mvn dependency:go-offline -B

COPY auth-service/src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder /app/target/auth-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 4000

ENTRYPOINT ["java","-jar","app.jar"]
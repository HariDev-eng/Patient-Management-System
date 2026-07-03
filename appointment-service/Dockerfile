FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

COPY proto ./proto

WORKDIR /build/proto
RUN mvn clean install -DskipTests

WORKDIR /build

COPY appointment-service ./appointment-service

WORKDIR /build/appointment-service

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk AS runner

WORKDIR /app

COPY --from=builder \
    /build/appointment-service/target/appointment-service-0.0.1-SNAPSHOT.jar \
    app.jar

EXPOSE 4007

ENTRYPOINT ["java", "-jar", "app.jar"]
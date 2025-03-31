FROM maven:3.8.8-eclipse-temurin-17 as builder

WORKDIR /app

COPY pom.xml .
COPY src ./src


RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim

WORKDIR /app


COPY --from=builder /app/target/*.jar app.jar
COPY Wallet_U8R9MXI3504ZG1FO ./Wallet_U8R9MXI3504ZG1FO

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

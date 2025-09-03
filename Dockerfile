# 1. Build-Stage
FROM maven:3.9 AS build
WORKDIR /app
COPY pom.xml .
COPY . .
RUN mvn clean package -DskipTests -Pproduction


# 2. Runtime-Stage
FROM openjdk:21-jdk-slim AS run
WORKDIR /app
COPY --from=build /app/target/*.jar famigo.jar
ENTRYPOINT ["java", "-jar", "famigo.jar"]



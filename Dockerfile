# Stage 1: Resolve dependencies
FROM maven:3.9.6-eclipse-temurin-17-alpine AS dependencies
WORKDIR /backend

# Copy all POM files
COPY pom.xml pom.xml
COPY main/pom.xml main/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY account-service/pom.xml account-service/pom.xml
COPY rating-service/pom.xml rating-service/pom.xml
COPY finance-service/pom.xml finance-service/pom.xml
COPY realtime-service/pom.xml realtime-service/pom.xml
COPY balance-service/pom.xml balance-service/pom.xml
COPY common/pom.xml common/pom.xml
COPY transportation-service/pom.xml transportation-service/pom.xml
COPY voucher-service/pom.xml voucher-service/pom.xml
COPY bonus-service/pom.xml bonus-service/pom.xml
COPY insurance-service/pom.xml insurance-service/pom.xml
# Resolve Maven dependencies
RUN mvn dependency:go-offline

# Stage 2: Build the application
FROM dependencies AS build

WORKDIR /backend
COPY . .
# Build the project without profiles
RUN mvn package -DskipTests

# Stage 3: Run the application
FROM openjdk:17 AS run

WORKDIR /backend
EXPOSE 8080
EXPOSE 9090
COPY --from=build /backend/main/target/main-1.0-SNAPSHOT.jar /backend/backend.jar
CMD ["java", "-jar", "backend.jar"]

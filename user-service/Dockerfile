# Stage 1: Build with Maven
FROM maven:3.8.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY user-service/pom.xml .
RUN mvn dependency:go-offline -B

COPY user-service/src ./src
RUN mvn package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
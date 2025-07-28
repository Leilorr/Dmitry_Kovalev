FROM maven:3.8.6-eclipse-temurin-17 as builder

WORKDIR /workspace
COPY pom.xml .
COPY discovery-service/pom.xml discovery-service/
RUN mvn -f discovery-service/pom.xml dependency:go-offline -B
COPY discovery-service/src discovery-service/src
RUN mvn -f discovery-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/discovery-service/target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
FROM maven:3.8.6-eclipse-temurin-17 as builder

WORKDIR /workspace
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
RUN mvn -f api-gateway/pom.xml dependency:go-offline -B
COPY api-gateway/src api-gateway/src
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
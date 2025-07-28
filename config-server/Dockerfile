FROM maven:3.8.6-eclipse-temurin-17 as builder

WORKDIR /workspace
COPY pom.xml .
COPY config-server/pom.xml config-server/
RUN mvn -f config-server/pom.xml dependency:go-offline -B
COPY config-server/src config-server/src
RUN mvn -f config-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/config-server/target/*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
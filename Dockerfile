# Runtime image for the Spring Boot application
FROM eclipse-temurin:21-jre

# The jar is produced by Maven in target/ before docker build is executed.
ARG JAR_FILE=target/*.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


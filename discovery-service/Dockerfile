FROM openjdk:21-jdk-slim

WORKDIR /app
COPY . /app

COPY "target/discovery-service-*-exec.jar" /app/app.jar
ADD src/main/resources/application.properties /app/application.properties

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/application.properties"]

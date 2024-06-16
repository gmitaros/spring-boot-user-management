FROM openjdk:21-slim

WORKDIR /app
COPY . /app

COPY target/gateway-service-0.0.1-SNAPSHOT-exec.jar /app/app.jar
ADD src/main/resources/application.properties /app/application.properties
ADD src/main/resources/application-docker.properties /app/application-docker.properties

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/application.properties"]

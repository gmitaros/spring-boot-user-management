FROM openjdk:21-jdk-slim

WORKDIR /app
COPY . /app

COPY "target/email-service-*-exec.jar" /app/app.jar
ADD src/main/resources/application.properties /app/application.properties
ADD src/main/resources/application-docker.properties /app/application-docker.properties
ADD src/main/resources/keys/private_key.pem /app/private_key.pem
ADD src/main/resources/keys/public_key.pem /app/public_key.pem
RUN chmod 644 /app/private_key.pem /app/public_key.pem
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.config.location=file:/app/application.properties"]

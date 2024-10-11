# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from your project to the container
COPY target/billing_software-0.0.1-SNAPSHOT.jar /app/billing_software-be.jar

# Expose the port your application runs on
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/billing_software-be.jar"]
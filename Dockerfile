FROM arm64v8/openjdk:17-jdk-slim-buster

MAINTAINER redon.kallaverja

# Set the working directory to /app
WORKDIR /app

# Copy all JAR files from build/libs directory into the container at /app
COPY build/libs/*.jar /app/

# Expose the port that the application will run on
EXPOSE 8080

# Specify the command to run on container start
CMD ["java", "-jar", "exchange-0.0.1-SNAPSHOT.jar"]

# Stage 1: Build the application using Maven and JDK 17
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
# Copy project metadata files first for better caching of dependencies
COPY pom.xml . 
# If you use a .mvn folder or any additional build configs, copy them as well:
# COPY .mvn .mvn
# Then copy the source code
COPY src ./src
# Build the project and skip tests for faster builds
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image using Alpine JDK 17
FROM eclipse-temurin:17-alpine
WORKDIR /app
# Copy the jar file from the build stage.
COPY --from=build /app/target/*.jar demo.jar
# Expose the port your app listens on (default for Spring Boot is 8080)
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "demo.jar"]

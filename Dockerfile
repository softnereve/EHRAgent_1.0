# Build stage
FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /app

# Copy the pom.xml and the libs folder first to leverage Docker cache
COPY pom.xml .
COPY libs ./libs

# Download dependencies (this will fail if system libs are not found, but we copied them)
RUN mvn dependency:go-offline -B || true

# Copy the source code
COPY src ./src

# Build the application, including system scope jars
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/ehr-agent.jar app.jar

# Expose the application port
EXPOSE 8282

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

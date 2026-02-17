# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Install Maven and Node.js
RUN apt-get update && apt-get install -y maven nodejs npm

# Copy all project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

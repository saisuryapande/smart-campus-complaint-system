FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application - specify the exact JAR name
RUN ./mvnw clean package -DskipTests

# Expose the port Render expects
EXPOSE 10000

# Run the application - use the exact JAR name
CMD ["java", "-jar", "target/complaintsystem-0.0.1-SNAPSHOT.jar"]
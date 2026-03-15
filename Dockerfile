FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy OTEL agent (downloaded locally)
COPY opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy your Spring Boot JAR
COPY target/springbootapp.jar app.jar

ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENTRYPOINT ["java", "-jar", "app.jar"]

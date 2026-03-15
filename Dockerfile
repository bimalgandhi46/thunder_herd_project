FROM eclipse-temurin:17-jre

WORKDIR /app

# Download the OpenTelemetry Java agent into the image
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar \
    /app/opentelemetry-javaagent.jar

# Copy your Spring Boot JAR
COPY target/springbootapp.jar app.jar

# Default OTEL config (Ansible will override)
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

ENTRYPOINT ["java", "-jar", "app.jar"]

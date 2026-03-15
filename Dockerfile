FROM eclipse-temurin:17-jre

WORKDIR /app

# Download the OpenTelemetry Java agent into /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# Copy your Spring Boot JAR into the container
COPY target/springbootapp.jar app.jar

# Set Java agent + OTEL config using JAVA_TOOL_OPTIONS
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar \
 -Dotel.exporter.otlp.endpoint=http://10.0.0.10:4318 \
 -Dotel.exporter.otlp.protocol=http/protobuf \
 -Dotel.resource.attributes=service.name=springbootapp"

# Start Spring Boot normally
ENTRYPOINT ["java", "-jar", "app.jar"]

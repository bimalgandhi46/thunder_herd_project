FROM eclipse-temurin:17-jre

WORKDIR /app

COPY target/springbootapp.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

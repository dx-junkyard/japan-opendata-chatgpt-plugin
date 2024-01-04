FROM eclipse-temurin:21.0.1_12-jdk

EXPOSE 8080

WORKDIR /app
COPY . .

RUN ./gradlew bootJar && cp build/libs/japan-opendata-chatgpt-plugin.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
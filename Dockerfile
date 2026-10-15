FROM eclipse-temurin:21-jre-ubi9-minimal

WORKDIR /app

COPY target/order-managment-lv-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
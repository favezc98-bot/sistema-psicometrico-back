# 1. Etapa de compilación usando Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render asigna el puerto dinámicamente mediante la variable PORT, Spring Boot usa el 8080 por defecto
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
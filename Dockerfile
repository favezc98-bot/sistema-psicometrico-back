# 1. Etapa de compilación usando Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Etapa de ejecución final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos el JAR que incluye todas las dependencias (Jackson, MariaDB, etc.)
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
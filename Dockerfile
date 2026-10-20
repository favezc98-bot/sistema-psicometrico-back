# 1. Etapa de compilación con Maven Assembly
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean compile assembly:single -DskipTests

# 2. Etapa de ejecución final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia el Fat JAR generado por el assembly plugin que contiene todas las dependencias
COPY --from=build /app/target/*-jar-with-dependencies.jar app.jar

# Variable de entorno con la contraseña para la base de datos en Aiven
ENV DB_PASSWORD=AVNS_90ICDIjqgS1Bf4Te9SO

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
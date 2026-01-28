# Usa una imagen base ligera con Java 17
# --- ETAPA 1: Compilación (Build) ---
# ETAPA 1: Compilación
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . .
# Compilamos directamente (el clean package ya descarga lo necesario)
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecución
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
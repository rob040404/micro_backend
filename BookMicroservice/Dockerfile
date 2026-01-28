# --- ETAPA 1: Compilación (Build) ---
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copiamos solo el pom.xml primero para aprovechar la caché de Docker de las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código fuente y compilamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# --- ETAPA 2: Ejecución (Run) ---
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Copiamos el JAR directamente desde la etapa anterior (sin necesidad de carpeta target local)
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 9002

# Ejecutamos con límites de memoria para evitar que Java intente usar más RAM de la que tiene la EC2
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
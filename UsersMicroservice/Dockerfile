# Imagen base con Java 17 (ligera y estándar)
FROM eclipse-temurin:17-jdk-focal

# Copia el JAR generado por Maven (Spring Boot)
COPY target/*.jar app.jar

# Expón el puerto que usa tu BookMicroservice
# (ajusta si no es 8082; común: 8082, 8080, 9001, etc.)
EXPOSE 9001

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
# Usa una imagen base ligera con Java 17
FROM eclipse-temurin:17-jdk-focal

# Copia el JAR generado por Maven (ajusta el nombre si es necesario)
COPY target/*.jar app.jar

# Expón el puerto que usa tu microservicio de seguridad
# (típicamente 8081, pero confirma el tuyo en application.yml)
EXPOSE 9000

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "/app.jar"]
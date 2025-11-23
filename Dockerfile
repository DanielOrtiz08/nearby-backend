# Usar una imagen base de Java (Eclipse Temurin) compatible y disponible
FROM eclipse-temurin:17-jdk

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar los archivos de construcción de Gradle
COPY build/libs/*.jar app.jar

# Exponer el puerto en el que la aplicación se ejecutará
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]


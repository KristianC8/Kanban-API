# Usa una imagen base de Java
FROM eclipse-temurin:21-jdk-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado al contenedor
COPY target/kanban-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por donde correrá la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

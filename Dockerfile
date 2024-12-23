# Usa una imagen base de Java
FROM eclipse-temurin:21-jdk-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copiamos el archivo pom.xml y ejecutamos Maven para construir la aplicación
COPY pom.xml pom.xml
RUN mvn package

# Copiamos el resto de los archivos de la aplicación
COPY src src

# Copia el archivo JAR generado al contenedor
COPY target/kanban-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto por donde correrá la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

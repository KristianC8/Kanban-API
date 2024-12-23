# Usa una imagen base de Java con Maven incluido
FROM maven:3.9.4-eclipse-temurin-21 AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copiamos los archivos de configuración y de código fuente
COPY pom.xml .
COPY src ./src

# Construye la aplicación y genera el archivo JAR
RUN mvn clean package -DskipTests

# Usa una imagen más liviana para ejecutar la aplicación
FROM eclipse-temurin:21-jdk-jammy

# Establece el directorio de trabajo
WORKDIR /app

# Copia el JAR generado desde la etapa de construcción
COPY --from=builder /app/target/kanban-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto de la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

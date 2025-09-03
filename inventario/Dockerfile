# Etapa 1: Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Crear archivo SQLite vacío si no existe
RUN touch /app/inventario.db

# Copiar JAR desde la etapa de build
COPY --from=build /app/target/inventario-0.0.1-SNAPSHOT.jar app.jar

# Puerto expuesto
EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]

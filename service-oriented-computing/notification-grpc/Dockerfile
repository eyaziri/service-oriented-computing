# Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copier les fichiers
COPY pom.xml .
COPY src ./src

# IMPORTANT : Copier les classes générées localement si elles existent
COPY target/generated-sources ./target/generated-sources/
COPY target/classes ./target/classes/

# Télécharger les dépendances
RUN mvn dependency:go-offline -B

# Compiler sans générer le protobuf (on l'a déjà)
RUN mvn package -DskipTests -Dprotobuf.skip=true -Dos.detected.classifier=linux-x86_64

# Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
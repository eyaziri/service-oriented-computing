# ========================================
# ÉTAPE 1: Build avec Maven
# ========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier de dépendances
COPY pom.xml .

# Télécharger toutes les dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et créer le JAR
RUN mvn clean package -DskipTests

# ========================================
# ÉTAPE 2: Runtime avec JRE
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/eureka-server-*.jar app.jar

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx256m -Xms128m"
ENV EUREKA_SERVER_PORT=8761

# Exposition du port
EXPOSE 8761

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
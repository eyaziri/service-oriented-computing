# ========================================
# ÉTAPE 1: Build avec Maven
# ========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# 1. Copier le fichier de dépendances
COPY pom.xml .

# 2. Télécharger toutes les dépendances (mise en cache)
RUN mvn dependency:go-offline -B

# 3. Copier le code source (y compris les fichiers protobuf qui sont déjà dans src/)
COPY src ./src

# 4. Vérifier que les fichiers protobuf sont bien copiés
RUN ls -la src/main/proto/ || echo "Vérification du dossier proto"

# 5. Générer les classes protobuf et compiler
RUN mvn clean compile package -DskipTests

# ========================================
# ÉTAPE 2: Runtime avec JRE
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Installer curl pour healthcheck
RUN apk add --no-cache curl

# Définir le répertoire de travail
WORKDIR /app

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/alert-orchestrator-*.jar app.jar

# Variables d'environnement
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xmx256m -Xms128m"
ENV SERVER_PORT=8084

# Exposition du port
EXPOSE 8084

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
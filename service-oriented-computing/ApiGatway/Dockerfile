# ========================================
# ÉTAPE 1: Build avec Maven
# ========================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# 1. Copier pom.xml d'abord (optimisation cache)
COPY pom.xml .

# 2. Télécharger les dépendances
RUN mvn dependency:go-offline -B

# 3. Copier le code source
COPY src ./src

# 4. Compiler et créer le JAR exécutable
RUN mvn clean package -DskipTests

# ========================================
# ÉTAPE 2: Runtime
# ========================================
FROM eclipse-temurin:17-jre-alpine

# Installer curl pour healthcheck
RUN apk add --no-cache curl

WORKDIR /app

# Créer utilisateur non-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copier le JAR exécutable
COPY --from=builder /app/target/api-gateway-*.jar app.jar

EXPOSE 8080

# Point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]
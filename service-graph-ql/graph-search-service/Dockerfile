# Étape 1: Build
FROM node:18-alpine AS builder

WORKDIR /app

# Copier les fichiers de configuration
COPY package*.json ./

# Installer les dépendances (utilisez npm install au lieu de npm ci)
RUN npm install --only=production

# Copier le code source
COPY . .

# Étape 2: Runtime
FROM node:18-alpine

WORKDIR /app

# Installer wget pour healthcheck
RUN apk add --no-cache wget

# Créer un utilisateur non-root
RUN addgroup -g 1001 -S nodejs
RUN adduser -S nodejs -u 1001
USER nodejs

# Copier depuis le builder
COPY --from=builder --chown=nodejs:nodejs /app /app

# Variables d'environnement
ENV NODE_ENV=production
ENV PORT=4000

# Exposition du port
EXPOSE 4000

# Commande de démarrage
CMD ["node", "index.js"]
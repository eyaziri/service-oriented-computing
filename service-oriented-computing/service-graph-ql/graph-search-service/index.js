require('dotenv').config();
const { ApolloServer } = require('apollo-server');
const fs = require('fs'); // <-- Ajoutez cette ligne
const path = require('path'); // <-- Ajoutez cette ligne
const resolvers = require('./schema/resolvers'); // <-- Plus besoin de typeDefs
const { sequelize } = require('./models/index');
const eurekaClient = require('./eureka-client');

const PORT = process.env.PORT || 4000;

// Lire le schéma GraphQL depuis le fichier .graphql
const typeDefs = fs.readFileSync(
  path.join(__dirname, 'schema', 'schema.graphql'),
  'utf8'
);

async function start() {
  try {
    console.log('🔌 Connexion à MySQL...');
    console.log(`   Host: ${process.env.DB_HOST}`);
    console.log(`   Database: ${process.env.DB_NAME}`);
    
    await sequelize.authenticate();
    console.log('✅ Connexion MySQL OK');

    // Sync la base
    if (process.env.NODE_ENV === 'development') {
      await sequelize.sync({ alter: true });
      console.log('🔄 Base synchronisée (mode développement)');
    } else {
      await sequelize.sync();
      console.log('📦 Base synchronisée (mode production)');
    }

    // Démarrer le client Eureka
    console.log('🔄 Enregistrement auprès d\'Eureka Server...');
    eurekaClient.start(error => {
      if (error) {
        console.error('❌ Erreur Eureka:', error);
      } else {
        console.log('✅ Service enregistré auprès d\'Eureka Server');
        console.log(`   Eureka Server: http://eureka-server:8761`);
        console.log(`   Service ID: graphql-service`);
      }
    });

    const server = new ApolloServer({
      typeDefs, // Utilise maintenant le schéma lu depuis le fichier
      resolvers,
      context: ({ req }) => ({}),
      introspection: true,
      cors: {
        origin: '*',
        credentials: true
      }
    });

    const { url } = await server.listen({ port: PORT });
    
    console.log('\n🎉 SERVEUR GRAPHQL PRÊT !');
    console.log('========================================');
    console.log(`🚀 GraphQL: ${url}`);
    console.log(`📡 Eureka Dashboard: http://eureka-server:8761`);
    console.log(`🔧 Apollo Sandbox: ${url}`);
    console.log('📄 Schéma SDL: ./schema/schema.graphql'); // <-- Ajoutez cette ligne
    console.log('========================================');

    // Gérer l'arrêt propre
    process.on('SIGINT', () => {
      console.log('\n🛑 Arrêt du service...');
      eurekaClient.stop();
      process.exit();
    });

  } catch (err) {
    console.error('❌ Erreur démarrage :', err.message);
    console.error('📋 Détails:', err.stack);
    process.exit(1);
  }
}

start();
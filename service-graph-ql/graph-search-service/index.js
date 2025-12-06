require('dotenv').config();
const { ApolloServer } = require('apollo-server');
const { ApolloServerPluginLandingPageGraphQLPlayground } = require('apollo-server-core');
const typeDefs = require('./schema/typeDefs');
const resolvers = require('./schema/resolvers');
const { sequelize } = require('./models/index');

const PORT = process.env.PORT || 4000;

async function start() {
  try {
    console.log('🔌 Connexion à MySQL...');
    await sequelize.authenticate();
    console.log('✅ Connexion MySQL OK');

    if (process.env.NODE_ENV === 'development') {
      await sequelize.sync({ alter: true });
    } else {
      await sequelize.sync();
    }

    const server = new ApolloServer({
      typeDefs,
      resolvers,
      context: ({ req }) => ({}),
      introspection: true,
      // Réactive l'ancien GraphQL Playground
      plugins: [ApolloServerPluginLandingPageGraphQLPlayground()],
      cors: {
        origin: '*',
        credentials: true
      }
    });

    const { url } = await server.listen({ port: PORT });
    
    console.log('\n🎉 GRAPHQL SERVER READY!');
    console.log('========================================');
    console.log(`🚀 GraphQL Playground: ${url}`);
    console.log(`📡 GraphQL Endpoint: ${url}graphql`);
    console.log('========================================');
    
    // Affiche des exemples de requêtes
    console.log('\n💡 Exemple de requêtes :');
    console.log('```graphql');
    console.log('query {');
    console.log('  searchAttractions(limit: 3) {');
    console.log('    id');
    console.log('    name');
    console.log('    city');
    console.log('    price');
    console.log('  }');
    console.log('}');
    console.log('```');

  } catch (err) {
    console.error('❌ Startup error:', err.message);
    process.exit(1);
  }
}

start();
require('dotenv').config();
const { ApolloServer } = require('apollo-server');
const typeDefs = require('./schema/typeDefs');
const resolvers = require('./schema/resolvers');
const { sequelize } = require('./models/index');
const Attraction = require('./models/attraction');

const PORT = process.env.PORT || 4000;

async function start() {
  try {
    await sequelize.authenticate();
    console.log('Connexion MySQL OK');

    await sequelize.sync({ alter: true }); 

    const server = new ApolloServer({
      typeDefs,
      resolvers,
      context: ({ req }) => {
        return {};
      }
    });

    server.listen({ port: PORT }).then(({ url }) => {
      console.log(`GraphQL server ready at ${url}`);
    });

  } catch (err) {
    console.error('Erreur démarrage :', err);
  }
}

start();

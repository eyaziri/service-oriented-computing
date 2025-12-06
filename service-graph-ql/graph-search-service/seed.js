require('dotenv').config();
const { sequelize } = require('./models/index');
const Attraction = require('./models/attraction');

async function seed() {
  try {
    await sequelize.authenticate();
    await sequelize.sync();

    const items = [
      { name: 'Musée du Bardo', city: 'Tunis', category: 'Culture', price: 12, tags: 'culture,history', lat: 36.8028, lng: 10.1665 },
      { name: 'Parc Belvédère', city: 'Tunis', category: 'Nature', price: 0, tags: 'nature,park', lat: 36.8188, lng: 10.1650 },
      { name: 'Medina de Tunis', city: 'Tunis', category: 'Histoire', price: 0, tags: 'culture,shopping', lat: 36.8065, lng: 10.1815 }
    ];

    for (const it of items) {
      await Attraction.create(it);
    }

    console.log('Seed terminé.');
    process.exit(0);
  } catch (err) {
    console.error(err);
    process.exit(1);
  }
}

seed();

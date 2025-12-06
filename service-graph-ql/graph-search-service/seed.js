require('dotenv').config();
const { sequelize } = require('./models/index');
const Attraction = require('./models/attraction');

async function seed() {
  try {
    console.log('Connexion à la base de données pour seed...');
    await sequelize.authenticate();
    console.log('✅ Connexion OK');
    
    // Sync la base
    await sequelize.sync({ force: true });
    console.log('✅ Tables créées/synchronisées');

    const items = [
      { 
        name: 'Musée du Bardo', 
        city: 'Tunis', 
        category: 'Culture', 
        price: 12, 
        tags: 'culture,history', 
        lat: 36.8028, 
        lng: 10.1665 
      },
      { 
        name: 'Parc Belvédère', 
        city: 'Tunis', 
        category: 'Nature', 
        price: 0, 
        tags: 'nature,park', 
        lat: 36.8188, 
        lng: 10.1650 
      },
      { 
        name: 'Medina de Tunis', 
        city: 'Tunis', 
        category: 'Histoire', 
        price: 0, 
        tags: 'culture,shopping', 
        lat: 36.8065, 
        lng: 10.1815 
      },
      { 
        name: 'Amphithéâtre d\'El Jem', 
        city: 'El Jem', 
        category: 'Histoire', 
        price: 10, 
        tags: 'roman,unesco', 
        lat: 35.2965, 
        lng: 10.7066 
      },
      { 
        name: 'Sidi Bou Saïd', 
        city: 'Tunis', 
        category: 'Culture', 
        price: 0, 
        tags: 'blue,white,village', 
        lat: 36.8686, 
        lng: 10.3417 
      }
    ];

    console.log(`Insertion de ${items.length} attractions...`);
    for (const it of items) {
      await Attraction.create(it);
      console.log(`✅ ${it.name} ajouté`);
    }

    console.log('✅ Seed terminé avec succès!');
    console.log(`Total: ${items.length} attractions insérées`);
    process.exit(0);
  } catch (err) {
    console.error('❌ Erreur lors du seed:', err.message);
    console.error('Stack:', err.stack);
    process.exit(1);
  }
}

seed();
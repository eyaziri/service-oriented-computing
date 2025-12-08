// Données simulées pour le développement lorsque l'API Gateway n'est pas disponible
const MockData = {
    attractions: [
        {
            id: 1,
            name: 'Musée du Bardo',
            city: 'Tunis',
            category: 'Culture',
            price: 12,
            description: 'Le plus important musée archéologique de Tunisie.',
            tags: 'musée,archéologie,histoire',
            lat: 36.8028,
            lng: 10.1665
        },
        {
            id: 2,
            name: 'Parc Belvédère',
            city: 'Tunis',
            category: 'Nature',
            price: 0,
            description: 'Le plus grand parc de Tunis.',
            tags: 'parc,nature,relaxation',
            lat: 36.8188,
            lng: 10.1650
        },
        {
            id: 3,
            name: 'Sidi Bou Saïd',
            city: 'Tunis',
            category: 'Culture',
            price: 0,
            description: 'Village pittoresque aux maisons bleues et blanches.',
            tags: 'village,bleu,blanc,photographie',
            lat: 36.8686,
            lng: 10.3417
        },
        {
            id: 4,
            name: 'Amphithéâtre d\'El Jem',
            city: 'El Jem',
            category: 'Histoire',
            price: 10,
            description: 'Troisième plus grand amphithéâtre romain au monde.',
            tags: 'romain,amphithéâtre,unesco',
            lat: 35.2965,
            lng: 10.7066
        },
        {
            id: 5,
            name: 'Medina de Tunis',
            city: 'Tunis',
            category: 'Culture',
            price: 0,
            description: 'Centre historique de Tunis, classé au patrimoine mondial de l\'UNESCO.',
            tags: 'médina,shopping,souks,unesco',
            lat: 36.8065,
            lng: 10.1815
        }
    ],
    
    monuments: [
        { id: 1, name: 'Musée du Bardo' },
        { id: 2, name: 'Amphithéâtre d\'El Jem' },
        { id: 3, name: 'Sidi Bou Saïd' }
    ],
    
    alerts: [
        {
            type: 'WEATHER',
            location: 'Tunis',
            message: 'Alerte canicule: températures attendues jusqu\'à 42°C',
            severity: 'HIGH',
            timestamp: new Date().toISOString()
        },
        {
            type: 'CROWD',
            location: 'Musée du Bardo',
            message: 'Forte affluence: attente estimée à 45 minutes',
            severity: 'MEDIUM',
            timestamp: new Date(Date.now() - 3600000).toISOString()
        }
    ]
};

// Simuler les appels API
const MockService = {
    async getAttractions(filters = {}) {
        console.log('📡 [MOCK] getAttractions appelé avec filters:', filters);
        
        let attractions = [...MockData.attractions];
        
        // Appliquer les filtres
        if (filters.city) {
            attractions = attractions.filter(a => a.city.toLowerCase().includes(filters.city.toLowerCase()));
        }
        
        if (filters.category) {
            attractions = attractions.filter(a => a.category === filters.category);
        }
        
        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            attractions = attractions.filter(a => 
                a.name.toLowerCase().includes(searchTerm) ||
                a.description.toLowerCase().includes(searchTerm) ||
                a.tags.toLowerCase().includes(searchTerm)
            );
        }
        
        // Simuler un délai réseau
        await new Promise(resolve => setTimeout(resolve, 300));
        
        return attractions;
    },
    
    async getAttractionById(id) {
        console.log(`📡 [MOCK] getAttractionById appelé avec id: ${id}`);
        
        await new Promise(resolve => setTimeout(resolve, 200));
        
        const attraction = MockData.attractions.find(a => a.id === id);
        if (!attraction) {
            throw new Error(`Attraction avec ID ${id} non trouvée`);
        }
        
        return attraction;
    }
};

// Utiliser les données mockées si le mode développement est activé
if (window.location.search.includes('mock=true')) {
    console.log('🔄 Mode développement: utilisation des données mockées');
    window.services = MockService;
}
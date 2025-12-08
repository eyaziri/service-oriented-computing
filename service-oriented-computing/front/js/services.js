// Service pour les appels API
const ApiService = {
    // Configuration
    config: {
        gateway: 'http://localhost:8080',
        endpoints: {
            attractions: '/api/attractions',
            graphql: '/graphql',
            cultural: '/soap',
            alerts: '/api/alerts'
        }
    },
    
    // Headers communs avec CORS
    getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
    },
    
    // === REST Service ===
    async getAttractions(filters = {}) {
        try {
            const queryString = new URLSearchParams(filters).toString();
            const url = `${this.config.gateway}${this.config.endpoints.attractions}${queryString ? '?' + queryString : ''}`;
            
            console.log(`📡 GET: ${url}`);
            
            const response = await fetch(url, {
                method: 'GET',
                headers: this.getHeaders(),
                mode: 'cors',
                credentials: 'omit'
            });
            
            console.log(`📥 Réponse: ${response.status} ${response.statusText}`);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
            
        } catch (error) {
            console.error('❌ Erreur getAttractions:', error);
            
            // Si échec, retourner des données mockées
            if (AppState && !AppState.isConnected) {
                console.log('🔄 Retour de données mockées');
                return this.getMockAttractions(filters);
            }
            
            throw error;
        }
    },
    
    getMockAttractions(filters = {}) {
        const mockData = [
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
            }
        ];
        
        // Filtrer les données mockées
        let filtered = [...mockData];
        
        if (filters.city) {
            filtered = filtered.filter(a => a.city === filters.city);
        }
        
        if (filters.category) {
            filtered = filtered.filter(a => a.category === filters.category);
        }
        
        if (filters.search) {
            const searchTerm = filters.search.toLowerCase();
            filtered = filtered.filter(a => 
                a.name.toLowerCase().includes(searchTerm) ||
                a.description.toLowerCase().includes(searchTerm)
            );
        }
        
        return filtered;
    },
    
    async createAttraction(attractionData) {
        try {
            const url = `${this.config.gateway}${this.config.endpoints.attractions}`;
            console.log(`📡 POST: ${url}`, attractionData);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(attractionData),
                mode: 'cors',
                credentials: 'omit'
            });
            
            console.log(`📥 Réponse: ${response.status} ${response.statusText}`);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            return await response.json();
            
        } catch (error) {
            console.error('❌ Erreur createAttraction:', error);
            
            // Simuler une création réussie en mode démo
            if (AppState && !AppState.isConnected) {
                console.log('🔄 Simulation de création');
                return {
                    id: Date.now(),
                    ...attractionData,
                    createdAt: new Date().toISOString()
                };
            }
            
            throw error;
        }
    },
    
    // === GraphQL Service ===
    async executeGraphQL(query, variables = {}) {
        try {
            const url = `${this.config.gateway}${this.config.endpoints.graphql}`;
            console.log(`📡 GraphQL: ${url}`);
            
            const response = await fetch(url, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify({ query, variables }),
                mode: 'cors',
                credentials: 'omit'
            });
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const result = await response.json();
            
            // Incrémenter le compteur
            if (AppState) {
                AppState.stats.queriesCount++;
                app.updateStats();
            }
            
            return result;
            
        } catch (error) {
            console.error('❌ Erreur GraphQL:', error);
            throw error;
        }
    },
    
    // Méthodes pour les boutons HTML
    async sendAlert() {
        const type = document.getElementById('alertType')?.value || 'INFO';
        const location = document.getElementById('alertLocation')?.value || 'Non spécifié';
        const message = document.getElementById('alertMessage')?.value || 'Alerte test';
        
        if (!location || !message) {
            alert('Veuillez remplir tous les champs');
            return;
        }
        
        try {
            const alertData = {
                type: type,
                location: location,
                message: message,
                timestamp: new Date().toISOString()
            };
            
            // En mode démo, simuler l'envoi
            if (AppState && !AppState.isConnected) {
                console.log('🔄 Simulation d\'alerte:', alertData);
                
                // Afficher l'alerte dans l'interface
                if (ui && ui.displayAlert) {
                    ui.displayAlert(alertData);
                }
                
                alert('✅ Alerte simulée avec succès!');
                return { success: true, mode: 'demo' };
            }
            
            // Envoi réel
            const url = `${this.config.gateway}${this.config.endpoints.alerts}/send`;
            const response = await fetch(url, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(alertData),
                mode: 'cors'
            });
            
            if (response.ok) {
                alert('✅ Alerte envoyée avec succès!');
                document.getElementById('alertMessage').value = '';
                return await response.json();
            } else {
                throw new Error(`HTTP ${response.status}`);
            }
            
        } catch (error) {
            console.error('❌ Erreur sendAlert:', error);
            alert(`❌ Erreur: ${error.message}`);
        }
    }
};

// Exposer le service globalement
window.services = ApiService;

// Méthodes globales pour les boutons HTML
window.sendAlert = function() {
    services.sendAlert();
};

window.executeGraphQL = function() {
    const query = document.getElementById('graphqlEditor')?.value;
    if (!query) {
        alert('Veuillez entrer une requête GraphQL');
        return;
    }
    
    services.executeGraphQL(query)
        .then(result => {
            const resultsDiv = document.getElementById('graphqlResults');
            if (resultsDiv) {
                resultsDiv.innerHTML = `
                    <div class="card">
                        <div class="card-body">
                            <pre class="mb-0">${JSON.stringify(result.data || result, null, 2)}</pre>
                        </div>
                    </div>
                `;
            }
        })
        .catch(error => {
            const resultsDiv = document.getElementById('graphqlResults');
            if (resultsDiv) {
                resultsDiv.innerHTML = `
                    <div class="alert alert-danger">
                        <strong>❌ Erreur:</strong> ${error.message}
                    </div>
                `;
            }
        });
};
// Configuration principale de l'application
const Config = {
    // URL de l'API Gateway - ESSAYEZ CES OPTIONS:
    API_GATEWAY: 'http://localhost:8080',
    // API_GATEWAY: 'http://127.0.0.1:8080', // Alternative
    
    ENDPOINTS: {
        // Test de connexion
        TEST: '/api/test/connection',
        TEST_ECHO: '/api/test/echo/hello',
        TEST_CORS: '/api/test/cors-test',
        
        // Services principaux
        REST: '/api/attractions',
        REST_ALT: '/attractions',  // Route alternative
        GRAPHQL: '/graphql',
        SOAP_WSDL: '/soap/wsdl',
        GRPC_ALERTS: '/api/alerts',
        
        // Monitoring
        HEALTH: '/actuator/health',
        ROUTES: '/actuator/gateway/routes',
        INFO: '/actuator/info'
    },
    
    // Configuration de requête
    REQUEST_CONFIG: {
        mode: 'cors',
        credentials: 'same-origin', // Essayer 'same-origin' si 'include' ne marche pas
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    },
    
    POLLING_INTERVAL: 10000, // 10 secondes
    RETRY_COUNT: 3,
    RETRY_DELAY: 1000
};

// État de l'application
const AppState = {
    isConnected: false,
    connectionAttempts: 0,
    services: {
        gateway: { status: 'UNKNOWN', lastCheck: null },
        rest: { status: 'UNKNOWN', lastCheck: null },
        soap: { status: 'UNKNOWN', lastCheck: null },
        grpc: { status: 'UNKNOWN', lastCheck: null },
        graphql: { status: 'UNKNOWN', lastCheck: null }
    },
    stats: {
        attractionsCount: 0,
        alertsCount: 0,
        queriesCount: 0,
        usersOnline: 1
    }
};

// Application principale
const App = {
    init() {
        console.log('🚀 Initialisation SOC Touristique');
        console.log('='.repeat(50));
        console.log(`🔗 API Gateway: ${Config.API_GATEWAY}`);
        console.log(`🌐 Frontend: ${window.location.origin}`);
        console.log('='.repeat(50));
        
        // Initialiser les écouteurs
        this.initEventListeners();
        
        // Test de connexion initial
        this.testConnection();
        
        // Mettre à jour l'heure
        this.updateTime();
        setInterval(() => this.updateTime(), 1000);
    },
    
    updateTime() {
        const now = new Date();
        const timeString = now.toLocaleTimeString();
        const timeElement = document.getElementById('currentTime');
        if (timeElement) {
            timeElement.textContent = timeString;
        }
    },
    
    async testConnection() {
        AppState.connectionAttempts++;
        console.log(`🔍 Test de connexion #${AppState.connectionAttempts}...`);
        
        // Afficher l'indicateur de chargement
        this.showConnectionStatus('Tentative de connexion...', 'warning');
        
        try {
            // Test 1: Endpoint de test simple
            const testUrl = `${Config.API_GATEWAY}${Config.ENDPOINTS.TEST}`;
            console.log(`📡 Test 1: ${testUrl}`);
            
            const response = await fetch(testUrl, {
                method: 'GET',
                headers: { 'Accept': 'application/json' },
                mode: 'cors',
                credentials: 'omit'
            });
            
            console.log(`📥 Réponse: ${response.status} ${response.statusText}`);
            
            if (response.ok) {
                const data = await response.json();
                console.log('✅ Test 1 réussi:', data.message);
                
                // Test 2: Endpoint echo
                const echoUrl = `${Config.API_GATEWAY}${Config.ENDPOINTS.TEST_ECHO}`;
                console.log(`📡 Test 2: ${echoUrl}`);
                
                const echoResponse = await fetch(echoUrl, {
                    method: 'GET',
                    headers: { 'Accept': 'application/json' },
                    mode: 'cors'
                });
                
                if (echoResponse.ok) {
                    const echoData = await echoResponse.json();
                    console.log('✅ Test 2 réussi:', echoData.echo);
                    
                    // Connexion réussie
                    this.handleConnectionSuccess(data);
                    
                } else {
                    throw new Error(`Echo test failed: ${echoResponse.status}`);
                }
                
            } else {
                throw new Error(`Test endpoint failed: ${response.status}`);
            }
            
        } catch (error) {
            console.error('❌ Erreur de connexion:', error);
            this.handleConnectionError(error);
        }
    },
    
    handleConnectionSuccess(data) {
        AppState.isConnected = true;
        AppState.connectionAttempts = 0;
        AppState.services.gateway.status = 'UP';
        AppState.services.gateway.lastCheck = new Date();
        
        // Mettre à jour l'interface
        this.updateGatewayStatus(true);
        this.showConnectionStatus('✅ Connecté à l\'API Gateway', 'success');
        this.log(`✅ Connexion établie: ${data.message}`, 'success');
        
        // Cacher le guide de dépannage
        this.hideTroubleshootingGuide();
        
        // Charger les données
        this.loadInitialData();
        
        // Afficher les infos de connexion
        this.showConnectionInfo(data);
    },
    
    handleConnectionError(error) {
        AppState.isConnected = false;
        AppState.services.gateway.status = 'DOWN';
        
        // Mettre à jour l'interface
        this.updateGatewayStatus(false);
        this.showConnectionStatus('❌ Déconnecté', 'danger');
        this.log(`❌ Erreur de connexion: ${error.message}`, 'error');
        
        // Afficher le guide de dépannage
        this.showTroubleshootingGuide();
        
        // Tentative automatique de reconnexion
        if (AppState.connectionAttempts < Config.RETRY_COUNT) {
            setTimeout(() => {
                this.testConnection();
            }, Config.RETRY_DELAY * AppState.connectionAttempts);
        }
    },
    
    showConnectionStatus(message, type) {
        const statusDiv = document.getElementById('connectionStatus');
        if (!statusDiv) return;
        
        statusDiv.innerHTML = `
            <div class="alert alert-${type} alert-dismissible fade show">
                <strong>${type === 'success' ? '✅' : type === 'warning' ? '⚠️' : '❌'} ${message}</strong>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `;
    },
    
    updateGatewayStatus(isConnected) {
        // Mettre à jour la navbar
        const gatewayElement = document.getElementById('gatewayStatus');
        if (gatewayElement) {
            gatewayElement.className = `badge ${isConnected ? 'bg-success' : 'bg-danger'}`;
            gatewayElement.innerHTML = `<i class="fas fa-${isConnected ? 'door-open' : 'door-closed'}"></i> Gateway`;
        }
        
        // Mettre à jour la sidebar
        const sidebarBadge = document.getElementById('gatewayStatusBadge');
        if (sidebarBadge) {
            sidebarBadge.textContent = isConnected ? 'UP' : 'DOWN';
            sidebarBadge.className = `status-badge ${isConnected ? 'bg-success' : 'bg-danger'}`;
            
            const indicator = sidebarBadge.previousElementSibling;
            if (indicator) {
                indicator.className = `status-indicator ${isConnected ? 'status-up' : 'status-down'}`;
            }
        }
    },
    
    showConnectionInfo(data) {
        const infoHtml = `
            <div class="card border-success mb-3">
                <div class="card-header bg-success text-white">
                    <h5><i class="fas fa-plug me-2"></i>Connexion établie</h5>
                </div>
                <div class="card-body">
                    <p><strong>Service:</strong> ${data.service}</p>
                    <p><strong>Version:</strong> ${data.version}</p>
                    <p><strong>Timestamp:</strong> ${data.timestamp}</p>
                    <p><strong>CORS:</strong> ${data.cors_enabled ? '✅ Activé' : '❌ Désactivé'}</p>
                    
                    <h6 class="mt-3">Endpoints disponibles:</h6>
                    <ul class="list-group">
                        ${Object.entries(data.endpoints || {}).map(([key, value]) => 
                            `<li class="list-group-item">
                                <code>${key}</code>: <a href="${Config.API_GATEWAY}${value}" target="_blank">${value}</a>
                            </li>`
                        ).join('')}
                    </ul>
                </div>
            </div>
        `;
        
        // Ajouter au début du contenu
        const contentCol = document.querySelector('.content-col');
        if (contentCol) {
            const existingInfo = document.querySelector('.connection-info');
            if (existingInfo) existingInfo.remove();
            
            const infoDiv = document.createElement('div');
            infoDiv.className = 'connection-info';
            infoDiv.innerHTML = infoHtml;
            contentCol.prepend(infoDiv);
        }
    },
    
    showTroubleshootingGuide() {
        const contentCol = document.querySelector('.content-col');
        if (!contentCol) return;
        
        // Supprimer l'ancien guide
        const oldGuide = document.querySelector('.troubleshooting-guide');
        if (oldGuide) oldGuide.remove();
        
        const guideHtml = `
            <div class="card border-danger">
                <div class="card-header bg-danger text-white">
                    <h5><i class="fas fa-exclamation-triangle me-2"></i>Guide de Dépannage</h5>
                </div>
                <div class="card-body">
                    <p>Impossible de se connecter à <code>${Config.API_GATEWAY}</code></p>
                    
                    <h6>Étapes de résolution:</h6>
                    <ol>
                        <li><strong>Vérifiez que l'API Gateway est démarré:</strong>
                            <pre class="bg-dark text-light p-2 mt-1">curl ${Config.API_GATEWAY}/actuator/health</pre>
                        </li>
                        <li><strong>Redémarrez l'API Gateway:</strong>
                            <pre class="bg-dark text-light p-2 mt-1">cd api-gateway && mvn spring-boot:run</pre>
                        </li>
                        <li><strong>Vérifiez les ports:</strong>
                            <ul>
                                <li>API Gateway: port 8080</li>
                                <li>Frontend: port ${window.location.port || '8000'}</li>
                            </ul>
                        </li>
                        <li><strong>Testez dans le terminal:</strong>
                            <pre class="bg-dark text-light p-2 mt-1">curl ${Config.API_GATEWAY}/api/test/connection</pre>
                        </li>
                        <li><strong>Ouvrez les outils de développement (F12)</strong> et vérifiez:
                            <ul>
                                <li>Onglet <strong>Console</strong> pour les erreurs</li>
                                <li>Onglet <strong>Network</strong> pour les requêtes</li>
                                <li>Vérifiez les en-têtes CORS</li>
                            </ul>
                        </li>
                    </ol>
                    
                    <div class="mt-3">
                        <button class="btn btn-warning me-2" onclick="app.testConnection()">
                            <i class="fas fa-sync-alt"></i> Réessayer
                        </button>
                        <button class="btn btn-info me-2" onclick="app.testDirectConnection()">
                            <i class="fas fa-terminal"></i> Tester avec curl
                        </button>
                        <button class="btn btn-secondary" onclick="app.enableDemoMode()">
                            <i class="fas fa-code"></i> Mode démo
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        const guideDiv = document.createElement('div');
        guideDiv.className = 'troubleshooting-guide mb-4';
        guideDiv.innerHTML = guideHtml;
        contentCol.prepend(guideDiv);
    },
    
    hideTroubleshootingGuide() {
        const guide = document.querySelector('.troubleshooting-guide');
        if (guide) guide.remove();
    },
    
    async loadInitialData() {
        if (!AppState.isConnected) {
            console.log('⚠️ API Gateway non connecté, mode démo activé');
            this.enableDemoMode();
            return;
        }
        
        console.log('📥 Chargement des données initiales...');
        
        try {
            // Charger les attractions
            await this.loadAttractions();
            
            // Charger les monuments
            await this.loadMonuments();
            
            // Vérifier les autres services
            await this.checkServices();
            
            // Mettre à jour les stats
            this.updateStats();
            
        } catch (error) {
            console.error('❌ Erreur chargement initial:', error);
        }
    },
    
    async loadAttractions() {
        try {
            // Essayer d'abord la route principale
            const url = `${Config.API_GATEWAY}${Config.ENDPOINTS.REST}`;
            console.log(`📡 Chargement attractions: ${url}`);
            
            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Accept': 'application/json' },
                mode: 'cors'
            });
            
            if (response.ok) {
                const attractions = await response.json();
                console.log(`✅ ${attractions.length} attractions chargées`);
                
                // Mettre à jour l'état
                AppState.services.rest.status = 'UP';
                AppState.stats.attractionsCount = attractions.length;
                
                // Afficher dans l'interface
                if (typeof ui !== 'undefined' && ui.displayAttractions) {
                    ui.displayAttractions(attractions);
                }
                
                this.log(`Attractions: ${attractions.length} chargées`, 'success');
                this.updateServiceStatus('rest', 'up');
                
            } else if (response.status === 404) {
                // Essayer la route alternative
                console.log('🔄 Essai route alternative...');
                await this.tryAlternativeAttractionsRoute();
                
            } else {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
        } catch (error) {
            console.error('❌ Erreur chargement attractions:', error);
            AppState.services.rest.status = 'DOWN';
            this.updateServiceStatus('rest', 'down');
        }
    },
    
    async tryAlternativeAttractionsRoute() {
        const altUrl = `${Config.API_GATEWAY}${Config.ENDPOINTS.REST_ALT}`;
        console.log(`📡 Essai route alternative: ${altUrl}`);
        
        try {
            const response = await fetch(altUrl, {
                method: 'GET',
                headers: { 'Accept': 'application/json' },
                mode: 'cors'
            });
            
            if (response.ok) {
                const attractions = await response.json();
                console.log(`✅ Route alternative OK: ${attractions.length} attractions`);
                
                // Mettre à jour la config
                Config.ENDPOINTS.REST = Config.ENDPOINTS.REST_ALT;
                
                // Mettre à jour l'état
                AppState.services.rest.status = 'UP';
                AppState.stats.attractionsCount = attractions.length;
                
                // Afficher
                if (ui && ui.displayAttractions) {
                    ui.displayAttractions(attractions);
                }
                
                this.log(`Route alternative utilisée: ${altUrl}`, 'warning');
                this.updateServiceStatus('rest', 'up');
                
            } else {
                throw new Error(`Route alternative also failed: ${response.status}`);
            }
            
        } catch (error) {
            console.error('❌ Route alternative échouée:', error);
            AppState.services.rest.status = 'DOWN';
            this.updateServiceStatus('rest', 'down');
        }
    },
    
    async loadMonuments() {
        // Données simulées pour le moment
        const monuments = [
            { id: 1, name: 'Musée du Bardo' },
            { id: 2, name: 'Amphithéâtre d\'El Jem' },
            { id: 3, name: 'Sidi Bou Saïd' },
            { id: 4, name: 'Cathédrale Saint-Vincent-de-Paul' },
            { id: 5, name: 'Medina de Tunis' }
        ];
        
        const select = document.getElementById('monumentSelect');
        if (select) {
            select.innerHTML = '<option value="">Sélectionner un monument...</option>';
            monuments.forEach(monument => {
                const option = document.createElement('option');
                option.value = monument.id;
                option.textContent = monument.name;
                select.appendChild(option);
            });
        }
        
        AppState.services.soap.status = 'UP';
        this.updateServiceStatus('soap', 'up');
    },
    
    async checkServices() {
        const services = [
            { name: 'graphql', endpoint: Config.ENDPOINTS.GRAPHQL },
            { name: 'grpc', endpoint: Config.ENDPOINTS.GRPC_ALERTS }
        ];
        
        for (const service of services) {
            await this.checkService(service.name, service.endpoint);
        }
    },
    
    async checkService(serviceName, endpoint) {
        try {
            const url = `${Config.API_GATEWAY}${endpoint}`;
            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Accept': 'application/json' },
                mode: 'cors'
            });
            
            // Pour GraphQL, une erreur 400 est normale (requête vide)
            if (response.ok || response.status === 400 || response.status === 405) {
                AppState.services[serviceName].status = 'UP';
                this.updateServiceStatus(serviceName, 'up');
                console.log(`✅ Service ${serviceName}: UP`);
            } else {
                AppState.services[serviceName].status = 'DOWN';
                this.updateServiceStatus(serviceName, 'down');
                console.log(`⚠️ Service ${serviceName}: DOWN (${response.status})`);
            }
            
        } catch (error) {
            console.error(`❌ Service ${serviceName}:`, error.message);
            AppState.services[serviceName].status = 'DOWN';
            this.updateServiceStatus(serviceName, 'down');
        }
    },
    
    updateServiceStatus(serviceName, status) {
        const badgeId = `${serviceName}StatusBadge`;
        const badge = document.getElementById(badgeId);
        
        if (badge) {
            badge.textContent = status.toUpperCase();
            badge.className = `status-badge ${status === 'up' ? 'bg-success' : 'bg-danger'}`;
            
            const indicator = badge.previousElementSibling;
            if (indicator && indicator.classList.contains('status-indicator')) {
                indicator.className = `status-indicator ${status === 'up' ? 'status-up' : 'status-down'}`;
            }
        }
    },
    
    updateStats() {
        // Mettre à jour la sidebar
        const stats = {
            'attractionsCount': AppState.stats.attractionsCount,
            'alertsCount': AppState.stats.alertsCount,
            'graphqlQueries': AppState.stats.queriesCount,
            'usersOnline': AppState.stats.usersOnline
        };
        
        Object.entries(stats).forEach(([id, value]) => {
            const element = document.getElementById(id);
            if (element) element.textContent = value;
        });
        
        // Mettre à jour le compteur de services
        const activeServices = Object.values(AppState.services)
            .filter(s => s.status === 'UP').length;
        
        const serviceCountElement = document.getElementById('serviceCount');
        if (serviceCountElement) {
            serviceCountElement.textContent = `${activeServices}/${Object.keys(AppState.services).length}`;
        }
    },
    
    enableDemoMode() {
        console.log('🔄 Activation du mode démo');
        
        // Données de démo
        const demoAttractions = [
            {
                id: 1,
                name: 'Musée du Bardo (Démo)',
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
                name: 'Parc Belvédère (Démo)',
                city: 'Tunis',
                category: 'Nature',
                price: 0,
                description: 'Le plus grand parc de Tunis.',
                tags: 'parc,nature,relaxation',
                lat: 36.8188,
                lng: 10.1650
            }
        ];
        
        // Mettre à jour l'état
        AppState.stats.attractionsCount = demoAttractions.length;
        AppState.services.rest.status = 'UP';
        AppState.services.soap.status = 'UP';
        AppState.services.graphql.status = 'UP';
        AppState.services.grpc.status = 'UP';
        
        // Afficher les données
        if (ui && ui.displayAttractions) {
            ui.displayAttractions(demoAttractions);
        }
        
        // Mettre à jour l'interface
        this.updateStats();
        this.updateServiceStatuses();
        
        // Afficher un message
        this.log('Mode démo activé - Données simulées', 'warning');
        
        const alert = document.createElement('div');
        alert.className = 'alert alert-warning alert-dismissible fade show m-3';
        alert.innerHTML = `
            <strong><i class="fas fa-code me-2"></i>Mode Démo Activé</strong>
            <p>Vous utilisez des données simulées. L'API Gateway n'est pas connecté.</p>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        const contentCol = document.querySelector('.content-col');
        if (contentCol) {
            contentCol.prepend(alert);
        }
    },
    
    updateServiceStatuses() {
        Object.keys(AppState.services).forEach(serviceName => {
            this.updateServiceStatus(serviceName, AppState.services[serviceName].status.toLowerCase());
        });
    },
    
    log(message, type = 'info') {
        const logsContainer = document.getElementById('systemLogs');
        if (!logsContainer) return;
        
        const time = new Date().toLocaleTimeString();
        const logEntry = document.createElement('div');
        logEntry.className = `log-entry log-${type}`;
        logEntry.innerHTML = `<span class="log-time">[${time}]</span> ${message}`;
        
        logsContainer.prepend(logEntry);
        
        // Limiter à 20 logs
        if (logsContainer.children.length > 20) {
            logsContainer.removeChild(logsContainer.lastChild);
        }
    },
    
    initEventListeners() {
        console.log('🎯 Écouteurs d\'événements initialisés');
    },
    
    // Méthodes utilitaires
    testDirectConnection() {
        const curlCommand = `curl ${Config.API_GATEWAY}/api/test/connection`;
        alert(`Exécutez cette commande dans votre terminal:\n\n${curlCommand}\n\nVérifiez que vous obtenez une réponse JSON.`);
    }
};

// Exposer l'application globalement
window.app = App;
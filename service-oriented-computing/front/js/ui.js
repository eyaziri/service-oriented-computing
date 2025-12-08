// Gestion de l'interface utilisateur
const UI = {
    // Cartes Leaflet
    map: null,
    markers: [],
    
    // Initialisation
    init() {
        this.initMap();
        this.initFormSubmissions();
        this.initTooltips();
        this.initNavigation();
    },
    
    // === Carte Leaflet ===
    initMap() {
        if (!document.getElementById('map')) return;
        
        this.map = L.map('map').setView([36.8065, 10.1815], 10);
        
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '© OpenStreetMap contributors',
            maxZoom: 18
        }).addTo(this.map);
    },
    
    addMarker(attraction) {
        if (!this.map) return;
        
        const marker = L.marker([attraction.lat, attraction.lng])
            .addTo(this.map)
            .bindPopup(`
                <div style="min-width: 200px;">
                    <b>${attraction.name}</b><br>
                    <small>${attraction.city} - ${attraction.category}</small><br>
                    <strong>Prix:</strong> ${attraction.price || 0} DT<br>
                    ${attraction.tags ? `<small>Tags: ${attraction.tags}</small>` : ''}
                </div>
            `);
        
        this.markers.push(marker);
        return marker;
    },
    
    clearMarkers() {
        this.markers.forEach(marker => marker.remove());
        this.markers = [];
    },
    
    showAttractionsOnMap(attractions) {
        this.clearMarkers();
        
        attractions.forEach(attraction => {
            this.addMarker(attraction);
        });
        
        // Ajuster la vue si on a des marqueurs
        if (attractions.length > 0 && this.markers.length > 0) {
            const group = new L.featureGroup(this.markers);
            this.map.fitBounds(group.getBounds().pad(0.1));
        }
    },
    
    // === Recherche d'attractions ===
    searchAttractions() {
        const searchTerm = document.getElementById('searchInput').value;
        const city = document.getElementById('cityFilter').value;
        const category = document.getElementById('categoryFilter').value;
        
        const filters = {};
        if (searchTerm) filters.search = searchTerm;
        if (city) filters.city = city;
        if (category) filters.category = category;
        
        this.showLoading('attractionsResults');
        
        services.getAttractions(filters)
            .then(attractions => {
                this.displayAttractions(attractions);
                this.showAttractionsOnMap(attractions);
                this.hideLoading('attractionsResults');
                
                // Mettre à jour le compteur dans la sidebar
                document.getElementById('attractionsCount').textContent = attractions.length;
            })
            .catch(error => {
                console.error('Erreur recherche:', error);
                this.hideLoading('attractionsResults');
                document.getElementById('attractionsResults').innerHTML = `
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle"></i>
                        Erreur lors de la recherche: ${error.message}
                    </div>
                `;
            });
    },
    
    displayAttractions(attractions) {
        const container = document.getElementById('attractionsResults');
        
        if (attractions.length === 0) {
            container.innerHTML = `
                <div class="text-center text-muted py-4">
                    <i class="fas fa-search fa-2x mb-3"></i>
                    <p>Aucune attraction trouvée</p>
                    <button class="btn btn-sm btn-primary" onclick="ui.loadAllAttractions()">
                        Voir toutes les attractions
                    </button>
                </div>
            `;
            return;
        }
        
        let html = '<div class="attractions-grid">';
        
        attractions.forEach(attraction => {
            const price = attraction.price ? `${attraction.price} DT` : 'Gratuit';
            
            html += `
                <div class="attraction-card">
                    <div class="attraction-card-header">
                        <h6 class="attraction-name">${attraction.name}</h6>
                        <span class="attraction-price">${price}</span>
                    </div>
                    <div class="attraction-card-body">
                        <div class="attraction-details">
                            <div>
                                <i class="fas fa-map-marker-alt text-muted"></i>
                                <span>${attraction.city}</span>
                            </div>
                            <div>
                                <i class="fas fa-tag text-muted"></i>
                                <span>${attraction.category}</span>
                            </div>
                            ${attraction.tags ? `
                            <div>
                                <i class="fas fa-hashtag text-muted"></i>
                                <small>${attraction.tags}</small>
                            </div>` : ''}
                        </div>
                        <div class="attraction-actions">
                            <button class="btn btn-sm btn-outline-primary" 
                                    onclick="ui.showAttractionDetails(${attraction.id})">
                                <i class="fas fa-info-circle"></i> Détails
                            </button>
                            <button class="btn btn-sm btn-outline-success" 
                                    onclick="ui.centerOnMap(${attraction.lat}, ${attraction.lng})">
                                <i class="fas fa-map-marker"></i> Carte
                            </button>
                        </div>
                    </div>
                </div>
            `;
        });
        
        html += '</div>';
        container.innerHTML = html;
    },
    
    async loadAllAttractions() {
        document.getElementById('searchInput').value = '';
        document.getElementById('cityFilter').value = '';
        document.getElementById('categoryFilter').value = '';
        this.searchAttractions();
    },
    
    centerOnMap(lat, lng) {
        if (this.map) {
            this.map.setView([lat, lng], 15);
        }
    },
    
    async showAttractionDetails(attractionId) {
        try {
            this.showLoading('attractionsResults');
            const attraction = await services.getAttractionById(attractionId);
            this.hideLoading('attractionsResults');
            
            // Créer un modal Bootstrap
            const modalHtml = `
                <div class="modal fade" id="attractionModal" tabindex="-1">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header bg-primary text-white">
                                <h5 class="modal-title">
                                    <i class="fas fa-landmark me-2"></i>${attraction.name}
                                </h5>
                                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h6>Informations</h6>
                                        <table class="table table-sm">
                                            <tr>
                                                <th><i class="fas fa-city me-2"></i>Ville:</th>
                                                <td>${attraction.city}</td>
                                            </tr>
                                            <tr>
                                                <th><i class="fas fa-tag me-2"></i>Catégorie:</th>
                                                <td>${attraction.category}</td>
                                            </tr>
                                            <tr>
                                                <th><i class="fas fa-money-bill me-2"></i>Prix:</th>
                                                <td>${attraction.price || 'Gratuit'} DT</td>
                                            </tr>
                                            <tr>
                                                <th><i class="fas fa-hashtag me-2"></i>Tags:</th>
                                                <td>${attraction.tags || 'Aucun'}</td>
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="col-md-6">
                                        <h6>Localisation</h6>
                                        <div id="modalMap" style="height: 200px; border-radius: 8px;"></div>
                                    </div>
                                </div>
                                ${attraction.description ? `
                                <div class="mt-3">
                                    <h6>Description</h6>
                                    <p>${attraction.description}</p>
                                </div>` : ''}
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                                    <i class="fas fa-times"></i> Fermer
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Ajouter le modal au DOM
            const modalContainer = document.createElement('div');
            modalContainer.innerHTML = modalHtml;
            document.body.appendChild(modalContainer);
            
            // Afficher le modal
            const modal = new bootstrap.Modal(document.getElementById('attractionModal'));
            modal.show();
            
            // Initialiser la carte dans le modal
            if (attraction.lat && attraction.lng) {
                const modalMap = L.map('modalMap').setView([attraction.lat, attraction.lng], 15);
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(modalMap);
                L.marker([attraction.lat, attraction.lng]).addTo(modalMap)
                    .bindPopup(`<b>${attraction.name}</b><br>${attraction.city}`);
            }
            
            // Nettoyer après fermeture
            document.getElementById('attractionModal').addEventListener('hidden.bs.modal', function() {
                document.body.removeChild(modalContainer);
            });
            
        } catch (error) {
            console.error('Erreur détails:', error);
            this.hideLoading('attractionsResults');
            alert(`Erreur: ${error.message}`);
        }
    },
    
    // === Gestion des formulaires ===
    initFormSubmissions() {
        // Formulaire de création d'attraction
        const createForm = document.getElementById('createForm');
        if (createForm) {
            createForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.createAttraction();
            });
        }
        
        // Recherche avec Enter
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    this.searchAttractions();
                }
            });
        }
    },
    
    async createAttraction() {
        const attractionData = {
            name: document.getElementById('attractionName').value,
            city: document.getElementById('attractionCity').value,
            category: document.getElementById('attractionCategory').value,
            price: parseFloat(document.getElementById('attractionPrice').value) || 0,
            description: document.getElementById('attractionDescription').value,
            tags: document.getElementById('attractionTags').value,
            lat: parseFloat(document.getElementById('attractionLat').value) || 36.8065,
            lng: parseFloat(document.getElementById('attractionLng').value) || 10.1815
        };
        
        // Validation simple
        if (!attractionData.name || !attractionData.city || !attractionData.category) {
            alert('Veuillez remplir les champs obligatoires: Nom, Ville et Catégorie');
            return;
        }
        
        try {
            const result = await services.createAttraction(attractionData);
            
            // Réinitialiser le formulaire
            document.getElementById('createForm').reset();
            
            // Afficher un message de succès
            this.showNotification('Succès', `Attraction "${result.name}" créée avec succès!`, 'success');
            
            // Recharger la liste des attractions
            this.searchAttractions();
            
        } catch (error) {
            console.error('Erreur création:', error);
            alert(`Erreur lors de la création: ${error.message}`);
        }
    },
    
    // === SOAP - Monuments ===
    async selectMonument(monumentId) {
        if (!monumentId) return;
        
        try {
            const info = await services.getHistoricalInfo(monumentId);
            const container = document.getElementById('monumentInfo');
            
            container.innerHTML = `
                <div class="monument-card">
                    <h5>${info.name}</h5>
                    <p class="text-muted">${info.description}</p>
                    <div class="monument-details">
                        <div class="detail-item">
                            <i class="fas fa-calendar-alt"></i>
                            <span><strong>Période:</strong> ${info.period}</span>
                        </div>
                        <div class="detail-item">
                            <i class="fas fa-star"></i>
                            <span><strong>Statut:</strong> ${info.status}</span>
                        </div>
                        <div class="detail-item">
                            <i class="fas fa-users"></i>
                            <span><strong>Visiteurs/an:</strong> ${info.annualVisitors.toLocaleString()}</span>
                        </div>
                        ${info.classification ? `
                        <div class="detail-item">
                            <i class="fas fa-award"></i>
                            <span><strong>Classification:</strong> ${info.classification}</span>
                        </div>` : ''}
                    </div>
                </div>
            `;
            
            // Mettre à jour la carte si disponible
            if (info.lat && info.lng) {
                this.centerOnMap(info.lat, info.lng);
            }
            
        } catch (error) {
            console.error('Erreur monument:', error);
            document.getElementById('monumentInfo').innerHTML = `
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    Erreur lors du chargement: ${error.message}
                </div>
            `;
        }
    },
    
    async compareSites() {
        const siteA = document.getElementById('siteA').value;
        const siteB = document.getElementById('siteB').value;
        
        if (!siteA || !siteB) {
            alert('Veuillez entrer deux sites à comparer');
            return;
        }
        
        try {
            const result = await services.compareSites(siteA, siteB);
            const container = document.getElementById('comparisonResult');
            
            container.innerHTML = `
                <div class="comparison-card">
                    <h5>Comparaison: ${siteA} vs ${siteB}</h5>
                    <div class="row mt-3">
                        <div class="col-md-6">
                            <div class="comparison-side">
                                <h6>${siteA}</h6>
                                <p><strong>Points forts:</strong> ${result.siteAStrengths || 'N/A'}</p>
                                <p><strong>Visiteurs:</strong> ${result.siteAVisitors || 'N/A'}</p>
                                <p><strong>Note:</strong> ${result.siteAScore || 'N/A'}/10</p>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="comparison-side">
                                <h6>${siteB}</h6>
                                <p><strong>Points forts:</strong> ${result.siteBStrengths || 'N/A'}</p>
                                <p><strong>Visiteurs:</strong> ${result.siteBVisitors || 'N/A'}</p>
                                <p><strong>Note:</strong> ${result.siteBScore || 'N/A'}/10</p>
                            </div>
                        </div>
                    </div>
                    <div class="comparison-verdict mt-3 p-3 bg-light rounded">
                        <h6><i class="fas fa-gavel me-2"></i>Verdict:</h6>
                        <p>${result.comparison || 'Aucune comparaison disponible'}</p>
                        <p class="mb-0"><strong>Recommandation:</strong> ${result.recommendation || 'N/A'}</p>
                    </div>
                </div>
            `;
            
        } catch (error) {
            console.error('Erreur comparaison:', error);
            alert(`Erreur: ${error.message}`);
        }
    },
    
    async getTouristStats() {
        const region = document.getElementById('regionInput').value;
        if (!region) {
            alert('Veuillez entrer une région');
            return;
        }
        
        try {
            const stats = await services.getTouristStats(region);
            const container = document.getElementById('statsResult');
            
            container.innerHTML = `
                <div class="stats-card">
                    <h6>Statistiques touristiques - ${region}</h6>
                    <div class="row text-center mt-3">
                        <div class="col">
                            <div class="stat-number">${stats.annualVisitors.toLocaleString()}</div>
                            <div class="stat-label">Visiteurs annuels</div>
                        </div>
                        <div class="col">
                            <div class="stat-number">${stats.growth}%</div>
                            <div class="stat-label">Croissance</div>
                        </div>
                        <div class="col">
                            <div class="stat-number">${stats.rating || 'N/A'}/10</div>
                            <div class="stat-label">Satisfaction</div>
                        </div>
                    </div>
                    ${stats.notes ? `
                    <div class="mt-3">
                        <small class="text-muted">${stats.notes}</small>
                    </div>` : ''}
                </div>
            `;
            
        } catch (error) {
            console.error('Erreur stats:', error);
            alert(`Erreur: ${error.message}`);
        }
    },
    
    // === GraphQL ===
    formatQuery() {
        const editor = document.getElementById('graphqlEditor');
        try {
            // Essayer de formater comme JSON d'abord
            const formatted = JSON.stringify(JSON.parse(editor.value), null, 2);
            editor.value = formatted;
        } catch (error) {
            // Sinon formater comme GraphQL
            let query = editor.value;
            
            // Ajouter des sauts de ligne après les accolades
            query = query.replace(/\{/g, '{\n  ');
            query = query.replace(/\}/g, '\n}');
            
            // Ajouter des sauts de ligne après les virgules dans les champs
            query = query.replace(/\,(?=\s*\w)/g, ',\n  ');
            
            // Ajouter des sauts de ligne après les parenthèses fermantes
            query = query.replace(/\)\s*\{/g, ') {\n  ');
            
            editor.value = query;
        }
    },
    
    clearQuery() {
        document.getElementById('graphqlEditor').value = '';
    },
    
    loadSample() {
        const sampleQuery = `query {
  searchAttractions(filter: { 
    city: "Tunis", 
    maxPrice: 50,
    category: "Culture"
  }, limit: 10) {
    id
    name
    city
    category
    price
    tags
    location {
      lat
      lng
    }
  }
}`;
        
        document.getElementById('graphqlEditor').value = sampleQuery;
    },
    
    loadQuery(queryType) {
        const queries = {
            freeAttractions: `query {
  searchAttractions(filter: { 
    city: "Tunis", 
    maxPrice: 0 
  }) {
    id
    name
    city
    category
    price
  }
}`,
            
            culturalSites: `query {
  searchAttractions(filter: { 
    category: "Culture" 
  }, limit: 10) {
    id
    name
    city
    price
    tags
    location {
      lat
      lng
    }
  }
}`,
            
            expensiveAttractions: `query {
  searchAttractions(filter: { 
    minPrice: 20 
  }) {
    id
    name
    city
    price
    category
  }
}`,
            
            tunisAttractions: `query {
  searchAttractions(filter: { 
    city: "Tunis" 
  }) {
    id
    name
    category
    price
    location {
      lat
      lng
    }
  }
}`
        };
        
        const query = queries[queryType];
        if (query) {
            document.getElementById('graphqlEditor').value = query;
        } else {
            alert('Requête non trouvée');
        }
    },
    
    // === Alertes gRPC ===
    displayAlert(alert) {
        const container = document.getElementById('alertsStream');
        
        const alertClass = `alert-${alert.type.toLowerCase()}`;
        const alertIcon = {
            WEATHER: 'fa-cloud-sun',
            CROWD: 'fa-users',
            SECURITY: 'fa-shield-alt',
            INFO: 'fa-info-circle'
        }[alert.type] || 'fa-exclamation-triangle';
        
        const severityClass = alert.severity ? `severity-${alert.severity.toLowerCase()}` : '';
        
        const alertElement = document.createElement('div');
        alertElement.className = `alert-item ${alertClass} ${severityClass}`;
        alertElement.innerHTML = `
            <div class="d-flex justify-content-between align-items-start">
                <div class="flex-grow-1">
                    <div class="d-flex align-items-center mb-1">
                        <i class="fas ${alertIcon} me-2"></i>
                        <strong>${this.translateAlertType(alert.type)}</strong>
                        <span class="badge bg-danger ms-2">${alert.severity || 'MEDIUM'}</span>
                    </div>
                    <div class="alert-location mb-1">
                        <i class="fas fa-map-marker-alt me-1"></i>
                        ${alert.location}
                    </div>
                    <p class="mb-0">${alert.message}</p>
                </div>
                <div class="text-end">
                    <small class="text-muted">${new Date(alert.timestamp).toLocaleTimeString()}</small>
                    <br>
                    <small class="text-muted">${new Date(alert.timestamp).toLocaleDateString()}</small>
                </div>
            </div>
        `;
        
        container.prepend(alertElement);
        
        // Animation d'entrée
        setTimeout(() => {
            alertElement.style.opacity = '1';
            alertElement.style.transform = 'translateX(0)';
        }, 10);
        
        // Mettre à jour le compteur dans la sidebar
        const alertsCount = container.querySelectorAll('.alert-item').length;
        document.getElementById('alertsCount').textContent = alertsCount;
        
        // Limiter à 15 alertes affichées
        if (container.children.length > 15) {
            container.removeChild(container.lastChild);
        }
        
        // Notification système si activée
        if (document.getElementById('enableAlerts')?.checked) {
            this.showNotification(
                `Alerte ${this.translateAlertType(alert.type)}`, 
                `${alert.message} (${alert.location})`,
                'warning'
            );
        }
    },
    
    translateAlertType(type) {
        const translations = {
            'WEATHER': 'Météo',
            'CROWD': 'Foule',
            'SECURITY': 'Sécurité',
            'INFO': 'Information'
        };
        return translations[type] || type;
    },
    
    // === Navigation ===
    initNavigation() {
        // Smooth scroll pour les liens de navigation
        document.querySelectorAll('.sidebar-nav .nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                
                // Retirer la classe active de tous les liens
                document.querySelectorAll('.sidebar-nav .nav-link').forEach(l => {
                    l.classList.remove('active');
                });
                
                // Ajouter la classe active au lien cliqué
                link.classList.add('active');
                
                // Faire défiler jusqu'à la section
                const targetId = link.getAttribute('href');
                const targetSection = document.querySelector(targetId);
                if (targetSection) {
                    targetSection.scrollIntoView({ 
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    },
    
    // === Tooltips ===
    initTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    },
    
    // === Notifications ===
    showNotification(title, message, type = 'info') {
        // Vérifier les permissions
        if (!("Notification" in window)) {
            console.log("Ce navigateur ne supporte pas les notifications desktop");
            return;
        }
        
        if (Notification.permission === "granted") {
            const icon = type === 'success' ? '✅' : type === 'warning' ? '⚠️' : 'ℹ️';
            new Notification(`${icon} ${title}`, {
                body: message,
                icon: `/assets/${type}.png`,
                tag: 'soc-notification'
            });
        } else if (Notification.permission !== "denied") {
            Notification.requestPermission().then(permission => {
                if (permission === "granted") {
                    this.showNotification(title, message, type);
                }
            });
        }
    },
    
    // === Loading spinner ===
    showLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = `
                <div class="loading-spinner">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Chargement...</span>
                    </div>
                    <p class="mt-2 text-muted">Chargement...</p>
                </div>
            `;
        }
    },
    
    hideLoading(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            const spinner = element.querySelector('.loading-spinner');
            if (spinner) {
                spinner.remove();
            }
        }
    },
    
    // === Mise à jour du statut des services ===
    updateServiceStatus(serviceName, status) {
        const badge = document.getElementById(`${serviceName}StatusBadge`);
        const indicator = document.querySelector(`#${serviceName}StatusBadge`).previousElementSibling;
        
        if (badge && indicator) {
            badge.textContent = status.toUpperCase();
            badge.className = `status-badge ${status === 'up' ? 'bg-success' : 'bg-danger'}`;
            
            indicator.className = `status-indicator ${status === 'up' ? 'status-up' : 'status-down'}`;
        }
    }
};

// Exposer l'UI globalement
window.ui = UI;
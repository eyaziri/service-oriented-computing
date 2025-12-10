import { Injectable, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject, BehaviorSubject } from 'rxjs';

export interface Alert {
  id: string;
  type: string;
  location: string;
  message: string;
  severity: number;
  timestamp: string | Date | number; // Plusieurs formats possibles
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private baseUrl = 'http://localhost:8080/api/alerts';
  private eventSource: EventSource | null = null;
  
  // Subjects pour les notifications
  private alertSubject = new Subject<Alert>();
  private alertsSubject = new BehaviorSubject<Alert[]>([]);
  private connectionStatusSubject = new BehaviorSubject<boolean>(false);
  
  // Observables publics
  public alerts$ = this.alertsSubject.asObservable();
  public alertReceived$ = this.alertSubject.asObservable();
  public connectionStatus$ = this.connectionStatusSubject.asObservable();

  constructor(
    private http: HttpClient,
    private ngZone: NgZone
  ) {}

  /**
   * Se connecter au flux SSE des alertes
   */
  connectToAlertStream(): void {
  if (this.eventSource) {
    this.disconnect();
  }

  const sseUrl = `${this.baseUrl}/sse`;
  console.log('🔗 Tentative de connexion SSE vers:', sseUrl);
  
  this.eventSource = new EventSource(sseUrl);

  // Écouter l'événement "connected" (avec c minuscule)
  this.eventSource.addEventListener('connected', (event: MessageEvent) => {
    this.ngZone.run(() => {
      try {
        const data = JSON.parse(event.data);
        console.log('🎉 Connexion SSE établie:', data.message);
        this.connectionStatusSubject.next(true);
        
        // Envoyer un événement de connexion au subject aussi
        const connectionAlert: Alert = {
          id: 'connection-' + Date.now(),
          type: 'SYSTEM',
          location: 'System',
          message: data.message,
          severity: 1,
          timestamp: data.timestamp.toString(),
          status: 'CONNECTED'
        };
        this.alertSubject.next(connectionAlert);
      } catch (error) {
        console.error('❌ Erreur parsing connected event:', error);
      }
    });
  });

  // Écouter les alertes (nom de l'événement peut être différent)
  this.eventSource.addEventListener('alert', (event: MessageEvent) => {
    this.handleAlertEvent(event);
  });

  // Écouter aussi les événements sans nom spécifique
  this.eventSource.addEventListener('message', (event: MessageEvent) => {
    console.log('📨 Message SSE reçu (sans nom):', event.data);
    // Essayer de parser comme alerte
    this.handleAlertEvent(event);
  });

  // Écouter tous les événements pour debug
  this.eventSource.addEventListener('test', (event: MessageEvent) => {
    console.log('🧪 Événement test reçu:', event.data);
    this.handleAlertEvent(event);
  });

  // Gérer l'ouverture de connexion
  this.eventSource.onopen = () => {
    this.ngZone.run(() => {
      console.log('✅ Connexion SSE ouverte');
      this.connectionStatusSubject.next(true);
    });
  };

  // Gérer les erreurs
  this.eventSource.onerror = (error) => {
    this.ngZone.run(() => {
      console.error('❌ Erreur SSE:', error);
      this.connectionStatusSubject.next(false);
      this.reconnect();
    });
  };
}

// Dans alert.service.ts - Remplacer la méthode handleAlertEvent

private handleAlertEvent(event: MessageEvent): void {
  try {
    const data = JSON.parse(event.data);
    console.log('📨 Données SSE reçues:', data);
    
    // Vérifier si c'est une alerte
    if (data.type || data.message) {
      // Normaliser le timestamp
      const normalizedTimestamp = this.normalizeTimestamp(data.timestamp);
      
      const alert: Alert = {
        id: data.id || data.alertId || 'alert-' + Date.now(),
        type: data.type || 'UNKNOWN',
        location: data.location || 'Unknown',
        message: data.message || 'No message',
        severity: data.severity || 1,
        timestamp: normalizedTimestamp,
        status: data.status || 'ACTIVE'
      };
      
      this.ngZone.run(() => {
        console.log('📢 Alerte traitée:', alert);
        this.alertSubject.next(alert);
        
        // CORRECTION: Récupérer les alertes actuelles
        const currentAlerts = this.alertsSubject.value;
        
        // Supprimer l'alerte si elle existe déjà (éviter les doublons)
        const filteredAlerts = currentAlerts.filter(a => a.id !== alert.id);
        
        // Ajouter la nouvelle alerte au début
        const updatedAlerts = [alert, ...filteredAlerts];
        
        // Trier par timestamp (récent → ancien)
        updatedAlerts.sort((a, b) => {
          const timeA = this.getTimeValue(a.timestamp);
          const timeB = this.getTimeValue(b.timestamp);
          return timeB - timeA;
        });
        
        // ⭐ IMPORTANT: Limiter à 3 alertes SEULEMENT
        const limitedAlerts = updatedAlerts.slice(0, 3);
        
        console.log(`✂️ Alertes limitées: ${updatedAlerts.length} → ${limitedAlerts.length}`);
        
        // Mettre à jour le subject avec SEULEMENT les 3 dernières
        this.alertsSubject.next(limitedAlerts);
      });
    } else {
      console.log('📨 Message SSE (pas une alerte):', data);
    }
  } catch (error) {
    console.error('❌ Erreur parsing SSE data:', error, 'Raw:', event.data);
  }
}

private normalizeTimestamp(timestamp: any): string | Date | number {
  if (!timestamp) {
    return new Date().toISOString();
  }
  
  // Si c'est déjà une string date
  if (typeof timestamp === 'string') {
    // Essayer de parser différentes formats
    try {
      // Format ISO (2025-12-09T16:30:45)
      if (timestamp.includes('T')) {
        return new Date(timestamp).toISOString();
      }
      
      // Format timestamp Unix (nombre en string)
      if (/^\d+$/.test(timestamp)) {
        const num = parseInt(timestamp, 10);
        // Si c'est en millisecondes
        if (num > 1000000000000) {
          return new Date(num).toISOString();
        }
        // Si c'est en secondes
        return new Date(num * 1000).toISOString();
      }
      
      // Autre format, retourner tel quel
      return timestamp;
    } catch (e) {
      console.warn('Erreur normalisation timestamp:', e);
      return new Date().toISOString();
    }
  }
  
  // Si c'est un nombre (timestamp Unix)
  if (typeof timestamp === 'number') {
    // Vérifier si c'est en millisecondes ou secondes
    if (timestamp > 1000000000000) {
      return new Date(timestamp).toISOString();
    } else {
      return new Date(timestamp * 1000).toISOString();
    }
  }
  
  // Fallback
  return new Date().toISOString();
}

// Méthode pour obtenir une valeur numérique du timestamp pour le tri
private getTimeValue(timestamp: any): number {
  if (!timestamp) return 0;
  
  try {
    if (typeof timestamp === 'string') {
      // Essayer de parser
      const date = new Date(timestamp);
      if (!isNaN(date.getTime())) {
        return date.getTime();
      }
      
      // Essayer de parser comme nombre
      const num = parseInt(timestamp, 10);
      if (!isNaN(num)) {
        return num > 1000000000000 ? num : num * 1000;
      }
      
      return 0;
    }
    
    if (typeof timestamp === 'number') {
      return timestamp > 1000000000000 ? timestamp : timestamp * 1000;
    }
    
    if (timestamp instanceof Date) {
      return timestamp.getTime();
    }
    
    return 0;
  } catch (e) {
    console.warn('Erreur conversion timestamp:', e);
    return 0;
  }
}

  /**
   * Se déconnecter du flux SSE
   */
  disconnect(): void {
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
      this.connectionStatusSubject.next(false);
      console.log('🔌 Déconnecté du flux d\'alertes');
    }
  }

  /**
   * Reconnexion automatique
   */
  private reconnect(): void {
    setTimeout(() => {
      console.log('🔄 Tentative de reconnexion...');
      this.connectToAlertStream();
    }, 3000);
  }

  /**
   * Récupérer les alertes actives pour une localisation
   */
  getActiveAlerts(location: string = ''): Observable<any> {
    const url = location ? `${this.baseUrl}/active?location=${location}` : `${this.baseUrl}/active`;
    return this.http.get(url);
  }

  /**
   * Tester la connexion gRPC
   */
  testConnection(): Observable<any> {
    return this.http.get(`${this.baseUrl}/test-connection`);
  }

  /**
   * Obtenir les statistiques
   */
  getStats(): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats`);
  }

  /**
   * Marquer une alerte comme résolue
   */
  resolveAlert(alertId: string): Observable<any> {
    // Si vous avez un endpoint pour résoudre les alertes dans l'orchestrateur
    // return this.http.put(`${this.baseUrl}/${alertId}/resolve`, {});
    
    // Sinon, filtrez simplement localement
    this.ngZone.run(() => {
      const currentAlerts = this.alertsSubject.value;
      const updatedAlerts = currentAlerts.filter(alert => alert.id !== alertId);
      this.alertsSubject.next(updatedAlerts);
    });
    
    return new Observable();
  }

  /**
   * Filtrer les alertes par localisation
   */
  filterAlertsByLocation(location: string): Alert[] {
    return this.alertsSubject.value.filter(alert => 
      alert.location.toLowerCase().includes(location.toLowerCase())
    );
  }

  /**
   * Filtrer les alertes par type
   */
  filterAlertsByType(type: string): Alert[] {
    return this.alertsSubject.value.filter(alert => 
      alert.type === type
    );
  }

  /**
   * Vider toutes les alertes
   */
  clearAlerts(): void {
    this.alertsSubject.next([]);
  }

  /**
   * Obtenir le nombre d'alertes actives
   */
  getActiveAlertCount(): number {
    return this.alertsSubject.value.filter(alert => alert.status === 'ACTIVE').length;
  }

  /**
   * Obtenir les alertes par gravité
   */
  getAlertsBySeverity(minSeverity: number = 1): Alert[] {
    return this.alertsSubject.value.filter(alert => 
      alert.severity >= minSeverity
    );
  }

  /**
   * Vérifier si une localisation a des alertes actives
   */
  hasAlertsForLocation(location: string): boolean {
    return this.alertsSubject.value.some(alert => 
      alert.location.toLowerCase().includes(location.toLowerCase()) && 
      alert.status === 'ACTIVE'
    );
  }
}
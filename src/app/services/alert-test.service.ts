import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlertTestService {
  private baseUrl = 'http://localhost:8080/api/alerts';

  constructor(private http: HttpClient) {}

  // Test seulement le SSE et le broadcast
  testSSEConnection(): Observable<any> {
    return new Observable(observer => {
      console.log('🧪 Test SSE avec EventSource');
      
      const eventSource = new EventSource(`${this.baseUrl}/sse`);
      let connected = false;
      
      // Timeout après 10 secondes
      const timeout = setTimeout(() => {
        if (!connected) {
          observer.error('Timeout: Aucune connexion SSE');
          eventSource.close();
        }
      }, 10000);
      
      eventSource.addEventListener('connected', (event) => {
        clearTimeout(timeout);
        connected = true;
        console.log('✅ SSE Connected:', JSON.parse(event.data));
        observer.next({ type: 'connected', data: JSON.parse(event.data) });
      });
      
      eventSource.addEventListener('alert', (event) => {
        console.log('🚨 SSE Alert Received:', JSON.parse(event.data));
        observer.next({ type: 'alert', data: JSON.parse(event.data) });
      });
      
      eventSource.onerror = (error) => {
        console.error('❌ SSE Error:', error);
        observer.error(error);
        eventSource.close();
      };
      
      // Fermer après 15 secondes
      setTimeout(() => {
        eventSource.close();
        observer.complete();
        console.log('🔌 Test SSE terminé');
      }, 15000);
      
      return () => {
        eventSource.close();
        clearTimeout(timeout);
      };
    });
  }

  // Envoyer un broadcast test (cela devrait fonctionner)
  sendBroadcastTest(): Observable<any> {
    console.log('📤 Envoi broadcast test...');
    return this.http.post(`${this.baseUrl}/broadcast-test`, {});
  }

  // Obtenir les alertes actives (cela devrait fonctionner)
  getActiveAlerts(): Observable<any> {
    console.log('📋 Récupération alertes actives...');
    return this.http.get(`${this.baseUrl}/active`);
  }
}
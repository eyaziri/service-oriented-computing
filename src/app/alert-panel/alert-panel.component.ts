// alert-panel.component.ts - VERSION CORRIGÉE COMPLÈTE

import { Component, OnInit, OnDestroy } from '@angular/core';
import { AlertService, Alert } from '../services/alert.service';
import { Subscription } from 'rxjs';
import { AlertTestService } from '../services/alert-test.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationService } from '../services/notification.service';
@Component({
  selector: 'app-alert-panel',
  templateUrl: './alert-panel.component.html',
  styleUrls: ['./alert-panel.component.scss']
})
export class AlertPanelComponent implements OnInit, OnDestroy {
  allAlerts: Alert[] = [];
  recentAlerts: Alert[] = [];
  showAlertPanel = true;
  debugMode = true;
  isConnected = false;

  private alertSubscription: Subscription | null = null;
  private connectionSubscription: Subscription | null = null;

  constructor(
    private snackBar: MatSnackBar,
    private alertService: AlertService,
    private alertTestService: AlertTestService,
    private notification:NotificationService

  ) {}

  ngOnInit(): void {
    console.log('🚀 Initialisation AlertPanel');
    
    this.testSSEConnection();
    this.alertService.connectToAlertStream();
    
    // S'abonner aux alertes du service (déjà limitées à 3)
    this.alertSubscription = this.alertService.alerts$.subscribe(alerts => {
      console.log('📡 Alertes du service:', alerts.length);
      
      // Le service envoie déjà les 3 dernières triées
      this.recentAlerts = alerts;
      this.allAlerts = alerts;
      
      if (this.recentAlerts.length > 0) {
        console.log('🕒 Alertes reçues:');
        this.recentAlerts.forEach((alert, i) => {
          console.log(`  ${i + 1}. ${alert.type} - ${this.formatTimestamp(alert.timestamp)}`);
        });
      }
    });
    
    this.connectionSubscription = this.alertService.connectionStatus$.subscribe(status => {
      console.log('📡 Statut connexion:', status ? 'Connecté' : 'Déconnecté');
      this.isConnected = status;
    });
    
    // Charger les alertes existantes
    this.loadActiveAlerts();
  }

  private sortAlertsByTimestamp(alerts: Alert[]): Alert[] {
    return [...alerts].sort((a, b) => {
      const timeA = this.getAlertTimeValue(a);
      const timeB = this.getAlertTimeValue(b);
      return timeB - timeA; // Descendant : récent → ancien
    });
  }

  private getAlertTimeValue(alert: Alert): number {
    if (!alert.timestamp) return 0;
    
    try {
      if (typeof alert.timestamp === 'string') {
        const date = new Date(alert.timestamp);
        if (!isNaN(date.getTime())) {
          return date.getTime();
        }
        
        const num = parseInt(alert.timestamp, 10);
        if (!isNaN(num)) {
          return num > 1000000000000 ? num : num * 1000;
        }
        
        return 0;
      }
      
      if (typeof alert.timestamp === 'number') {
        return alert.timestamp > 1000000000000 ? alert.timestamp : alert.timestamp * 1000;
      }
      
      if (alert.timestamp instanceof Date) {
        return alert.timestamp.getTime();
      }
      
      return 0;
    } catch (e) {
      console.warn('Erreur conversion timestamp:', e);
      return 0;
    }
  }

  formatTimestamp(timestamp: any): string {
    if (!timestamp) return 'N/A';
    
    try {
      let date: Date;
      
      if (typeof timestamp === 'string') {
        date = new Date(timestamp);
      } else if (typeof timestamp === 'number') {
        date = timestamp > 1000000000000 ? 
          new Date(timestamp) : new Date(timestamp * 1000);
      } else if (timestamp instanceof Date) {
        date = timestamp;
      } else {
        return 'Format invalide';
      }
      
      if (isNaN(date.getTime())) {
        return 'Date invalide';
      }
      
      const now = new Date();
      const isToday = date.getDate() === now.getDate() && 
                     date.getMonth() === now.getMonth() && 
                     date.getFullYear() === now.getFullYear();
      
      if (isToday) {
        return `Aujourd'hui ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } else {
        return `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      }
    } catch (e) {
      console.warn('Erreur formatage timestamp:', e);
      return String(timestamp);
    }
  }

  testSSEConnection(): void {
    this.alertTestService.testSSEConnection().subscribe({
      next: (result: any) => {
        if (result.type === 'connected') {
          this.snackBar.open('✅ Connecté au flux SSE', 'OK', { duration: 3000 });
        } else if (result.type === 'alert') {
          this.snackBar.open(`🚨 Alerte reçue: ${result.data.type}`, 'OK', { duration: 3000 });
        }
      },
      error: (error) => {
        console.error('Test SSE échoué:', error);
        this.snackBar.open('❌ Connexion SSE échouée', 'OK', { duration: 5000 });
      }
    });
  }

  viewAllAlerts(): void {
    console.log('Voir toutes les alertes:', this.allAlerts);
    this.snackBar.open(
      `Vous avez ${this.allAlerts.length} alertes récentes`,
      'OK',
      { duration: 3000 }
    );
  }

  loadActiveAlerts(): void {
    this.notification.getActiveAlerts().subscribe({
      next: (response) => {
        console.log('✅ Réponse backend:', response);
        
        if (response.alerts && response.alerts.length > 0) {
          // Mapper et normaliser toutes les alertes
          const allAlertsFromBackend: Alert[] = response.alerts.map((alertData: any) => {
            const normalizedTimestamp = this.normalizeTimestampFromResponse(alertData.timestamp);
            
            return {
              id: alertData.alertId || alertData.id,
              type: alertData.type,
              location: alertData.location,
              message: alertData.message,
              severity: alertData.severity,
              timestamp: normalizedTimestamp,
              status: alertData.status || 'ACTIVE'
            };
          });
          
          console.log(`📊 Total alertes backend: ${allAlertsFromBackend.length}`);
          
          // Trier PAR TIMESTAMP (pas par ordre de BDD)
          const sortedAlerts = this.sortAlertsByTimestamp(allAlertsFromBackend);
          
          console.log('📅 Alertes triées par timestamp:');
          sortedAlerts.forEach((alert, i) => {
            const ts = this.formatTimestamp(alert.timestamp);
            console.log(`  ${i + 1}. ${ts} - ${alert.type} (${alert.location})`);
          });
          
          // Prendre SEULEMENT les 3 plus récentes
          const top3 = sortedAlerts.slice(0, 3);
          
          console.log(`✂️ Top 3 alertes: ${sortedAlerts.length} → ${top3.length}`);
          console.log('🎯 Les 3 dernières alertes affichées:');
          top3.forEach((alert, i) => {
            console.log(`  ${i + 1}. ${this.formatTimestamp(alert.timestamp)} - ${alert.type}`);
          });
          
          // Mettre à jour l'interface
          this.allAlerts = top3;
          this.recentAlerts = top3;
        } else {
          console.log('⚠️ Aucune alerte reçue du backend');
        }
      },
      error: (error) => {
        console.error('❌ Erreur chargement alertes:', error);
      }
    });
  }

  private normalizeTimestampFromResponse(timestamp: any): string {
    if (!timestamp) return new Date().toISOString();
    
    if (typeof timestamp === 'string' && timestamp.includes('T')) {
      try {
        const date = new Date(timestamp);
        if (!isNaN(date.getTime())) {
          return timestamp;
        }
      } catch (e) {}
    }
    
    if (typeof timestamp === 'number' || /^\d+$/.test(String(timestamp))) {
      const num = typeof timestamp === 'number' ? timestamp : parseInt(timestamp, 10);
      if (!isNaN(num)) {
        const milliseconds = num > 1000000000000 ? num : num * 1000;
        return new Date(milliseconds).toISOString();
      }
    }
    
    return new Date().toISOString();
  }

  getCurrentTime(): string {
    const now = new Date();
    return `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}:${now.getSeconds().toString().padStart(2, '0')}`;
  }

  getTimeAgo(timestamp: any): string {
    if (!timestamp) return 'N/A';
    
    const now = new Date();
    let alertTime: Date;
    
    try {
      if (typeof timestamp === 'string') {
        alertTime = new Date(timestamp);
      } else if (typeof timestamp === 'number') {
        alertTime = timestamp > 1000000000000 ? 
          new Date(timestamp) : new Date(timestamp * 1000);
      } else if (timestamp instanceof Date) {
        alertTime = timestamp;
      } else {
        return '?';
      }
      
      if (isNaN(alertTime.getTime())) return '?';
      
      const diffMs = now.getTime() - alertTime.getTime();
      const diffSec = Math.floor(diffMs / 1000);
      const diffMin = Math.floor(diffSec / 60);
      const diffHour = Math.floor(diffMin / 60);
      
      if (diffSec < 60) return 'À l\'instant';
      if (diffMin < 60) return `Il y a ${diffMin} min`;
      if (diffHour < 24) return `Il y a ${diffHour} h`;
      
      const diffDays = Math.floor(diffHour / 24);
      return `Il y a ${diffDays} j`;
    } catch (e) {
      return '?';
    }
  }

  sendTestBroadcast(): void {
    console.log('🧪 Envoi broadcast test...');
    
    this.alertTestService.sendBroadcastTest().subscribe({
      next: (response: any) => {
        console.log('✅ Broadcast test réussi:', response);
        this.snackBar.open(`✅ Broadcast envoyé (${response.connectedClients} clients)`, 'OK', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('❌ Broadcast test échoué:', error);
        this.snackBar.open('❌ Échec du broadcast', 'OK', { duration: 3000 });
      }
    });
  }

  ngOnDestroy(): void {
    if (this.alertSubscription) this.alertSubscription.unsubscribe();
    if (this.connectionSubscription) this.connectionSubscription.unsubscribe();
    this.alertService.disconnect();
  }

  toggleAlertPanel(): void {
    this.showAlertPanel = !this.showAlertPanel;
  }

  getSeverityIcon(severity: number): string {
    const icons = ['info', 'warning', 'warning_amber', 'error_outline', 'error'];
    return icons[severity - 1] || 'warning';
  }

  getSeverityLabel(severity: number): string {
    const labels = ['Faible', 'Moyenne', 'Haute', 'Critique', 'Urgent'];
    return labels[severity - 1] || 'Moyenne';
  }

  getSeverityColor(severity: number): string {
    const colors = ['primary', 'accent', 'warn', 'warn', 'warn'];
    return colors[severity - 1] || 'warn';
  }

  showAlertDetails(alert: Alert): void {
    console.log('Détails alerte:', alert);
    
    if (Notification.permission === 'granted' && alert.severity >= 3) {
      new Notification(`Alerte ${alert.type}`, {
        body: `${alert.message}\n\nLocalisation: ${alert.location}`,
        icon: 'assets/alert-icon.png'
      });
    }
  }

  dismissAlert(alertId: string): void {
    this.alertService.resolveAlert(alertId).subscribe();
  }

  clearAlerts(): void {
    this.alertService.clearAlerts();
  }
}
import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../services/notification.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notifications-panel',
  templateUrl: './notifications-panel.component.html',
  styleUrls: ['./notifications-panel.component.scss']
})
export class NotificationsPanelComponent implements OnInit {
  notifications: any[] = [];
  loading = false;

  constructor(
    private notificationService: NotificationService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadActiveNotifications();
  }

  loadActiveNotifications() {
    this.loading = true;
    this.notificationService.getActiveAlerts().subscribe({
      next: (response) => {
        this.notifications = response.alerts || [];
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading notifications:', error);
        this.loading = false;
      }
    });
  }

  resolveNotification(alertId: string) {
    this.notificationService.resolveAlert(alertId).subscribe({
      next: () => {
        this.loadActiveNotifications();
        this.snackBar.open('Notification résolue', 'Fermer', {
          duration: 2000
        });
      },
      error: (error) => {
        console.error('Error resolving notification:', error);
        this.snackBar.open('Erreur lors de la résolution', 'Fermer', {
          duration: 2000
        });
      }
    });
  }

  getSeverityColor(severity: number): string {
    const colors = ['primary', 'accent', 'warn', 'warn', 'warn'];
    return colors[severity - 1] || 'warn';
  }

  getSeverityIcon(severity: number): string {
    const icons = ['info', 'warning', 'warning_amber', 'error_outline', 'error'];
    return icons[severity - 1] || 'warning';
  }
}
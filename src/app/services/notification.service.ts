import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Notification {
  alertId?: string;
  type: string;
  location: string;
  message: string;
  severity: number;
  status?: string;
  timestamp?: string;
  resolvedAt?: string;
}

export interface CreateNotificationRequest {
  type: string;
  location: string;
  message: string;
  severity: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private baseUrl = 'http://localhost:8080/api/notification';

  constructor(private http: HttpClient) {}

  createAlert(request: CreateNotificationRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/alerts`, request);
  }

  getActiveAlerts(): Observable<any> {
    return this.http.get(`${this.baseUrl}/alerts/active`);
  }

  getAlertsByLocation(location: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/alerts/location/${location}`);
  }

  getAlertsByType(type: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/alerts/type/${type}`);
  }

  resolveAlert(alertId: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/alerts/${alertId}/resolve`, {});
  }

  getStats(): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats`);
  }
}
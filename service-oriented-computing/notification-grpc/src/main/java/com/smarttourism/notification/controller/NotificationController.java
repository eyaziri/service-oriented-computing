package com.smarttourism.notification.controller;

import com.smarttourism.notification.dto.CreateNotificationRequest;
import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping("/alerts")
    public ResponseEntity<Map<String, Object>> createAlert(@RequestBody CreateNotificationRequest request) {
        Notification alert = notificationService.createAlert(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Alert created successfully");
        response.put("alertId", alert.getAlertId());
        response.put("alert", convertToMap(alert));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getAllAlerts() {
        List<Notification> alerts = notificationService.getAllAlerts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", alerts.size());
        response.put("alerts", alerts.stream()
            .map(this::convertToMap)
            .toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alerts/active")
    public ResponseEntity<Map<String, Object>> getActiveAlerts() {
        List<Notification> alerts = notificationService.getActiveAlerts();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("count", alerts.size());
        response.put("alerts", alerts.stream()
            .map(this::convertToMap)
            .toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alerts/location/{location}")
    public ResponseEntity<Map<String, Object>> getAlertsByLocation(@PathVariable String location) {
        List<Notification> alerts = notificationService.getAlertsByLocation(location);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("location", location);
        response.put("count", alerts.size());
        response.put("alerts", alerts.stream()
            .map(this::convertToMap)
            .toList());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/alerts/type/{type}")
    public ResponseEntity<Map<String, Object>> getAlertsByType(@PathVariable String type) {
        List<Notification> alerts = notificationService.getAlertsByType(type);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("type", type);
        response.put("count", alerts.size());
        response.put("alerts", alerts.stream()
            .map(this::convertToMap)
            .toList());
        
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/alerts/{alertId}/resolve")
    public ResponseEntity<Map<String, Object>> resolveAlert(@PathVariable String alertId) {
        Notification alert = notificationService.resolveAlert(alertId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Alert resolved successfully");
        response.put("alert", convertToMap(alert));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long activeCount = notificationService.countActiveAlerts();
        List<Notification> recentAlerts = notificationService.getRecentAlerts(10);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("activeAlerts", activeCount);
        response.put("recentAlerts", recentAlerts.stream()
            .map(this::convertToMap)
            .toList());
        
        return ResponseEntity.ok(response);
    }
    
    // REMOVED: /health and /info endpoints - they're already in HealthController
    // If you need them here, use different paths like /service-health or /service-info
    
    // Méthode utilitaire pour convertir Notification en Map
    private Map<String, Object> convertToMap(Notification alert) {
        Map<String, Object> map = new HashMap<>();
        map.put("alertId", alert.getAlertId());
        map.put("type", alert.getType().name());
        map.put("location", alert.getLocation());
        map.put("message", alert.getMessage());
        map.put("severity", alert.getSeverity());
        map.put("status", alert.getStatus().name());
        map.put("timestamp", alert.getTimestamp().toString());
        if (alert.getResolvedAt() != null) {
            map.put("resolvedAt", alert.getResolvedAt().toString());
        }
        return map;
    }
}
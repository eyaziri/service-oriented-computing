package com.smarttourism.notification.controller;

import com.smarttourism.notification.dto.CreateNotificationRequest;
import com.smarttourism.notification.dto.NotificationResponse;
import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @PostMapping("/alert")
    public ResponseEntity<NotificationResponse> createAlert(@RequestBody CreateNotificationRequest request) {
        Notification alert = notificationService.createAlert(
            request.getType(),
            request.getLocation(),
            request.getMessage(),
            request.getSeverity()
        );
        
        return ResponseEntity.ok(toResponse(alert));
    }

    @GetMapping("/active")
    public ResponseEntity<List<NotificationResponse>> getActiveAlerts() {
        List<Notification> alerts = notificationService.getAllActiveAlerts();
        List<NotificationResponse> responses = alerts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active/location/{location}")
    public ResponseEntity<List<NotificationResponse>> getActiveAlertsByLocation(@PathVariable String location) {
        List<Notification> alerts = notificationService.getActiveAlertsByLocation(location);
        List<NotificationResponse> responses = alerts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getActiveAlertsByType(@PathVariable String type) {
        List<Notification> alerts = notificationService.getAlertsByType(type);
        List<NotificationResponse> responses = alerts.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/resolve/{alertId}")
    public ResponseEntity<NotificationResponse> resolveAlert(@PathVariable String alertId) {
        Notification alert = notificationService.resolveAlert(alertId);
        return ResponseEntity.ok(toResponse(alert));
    }

    private NotificationResponse toResponse(Notification alert) {
        return new NotificationResponse(
            alert.getAlertId(),
            alert.getType().toString(),
            alert.getLocation(),
            alert.getMessage(),
            alert.getSeverity(),
            alert.getStatus().toString(),
            alert.getTimestamp().format(FORMATTER)
        );
    }
}
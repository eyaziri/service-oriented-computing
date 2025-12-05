package com.smarttourism.notification.service;

import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    public Notification createAlert(String type, String location, String message, int severity) {
        Notification alert = new Notification();
        alert.setAlertId(generateAlertId());
        alert.setType(Notification.AlertType.valueOf(type.toUpperCase()));
        alert.setLocation(location);
        alert.setMessage(message);
        alert.setSeverity(Math.min(Math.max(severity, 1), 5)); // Clamp 1-5
        alert.setStatus(Notification.AlertStatus.ACTIVE);
        alert.setTimestamp(LocalDateTime.now());
        return repository.save(alert);
    }

    public List<Notification> getActiveAlertsByLocation(String location) {
        return repository.findByLocationAndStatus(location, Notification.AlertStatus.ACTIVE);
    }

    public List<Notification> getAllActiveAlerts() {
        return repository.findByStatus(Notification.AlertStatus.ACTIVE);
    }

    public Notification resolveAlert(String alertId) {
        return repository.findByAlertId(alertId).map(alert -> {
            alert.setStatus(Notification.AlertStatus.RESOLVED);
            alert.setResolvedAt(LocalDateTime.now());
            return repository.save(alert);
        }).orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
    }

    public List<Notification> getAlertsByType(String type) {
        return repository.findByTypeAndStatus(
            Notification.AlertType.valueOf(type.toUpperCase()),
            Notification.AlertStatus.ACTIVE
        );
    }

    public List<Notification> getHighSeverityAlerts(int threshold) {
        return repository.findBySeverityGreaterThanEqual(threshold);
    }

    private String generateAlertId() {
        return "ALERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
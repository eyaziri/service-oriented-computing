package com.smarttourism.notification.service;

import com.smarttourism.notification.dto.CreateNotificationRequest;
import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    
    public Notification createAlert(CreateNotificationRequest request) {
        log.info("Creating alert: type={}, location={}", request.getType(), request.getLocation());
        
        Notification alert = new Notification();
        alert.setAlertId(UUID.randomUUID().toString());
        alert.setType(Notification.AlertType.valueOf(request.getType()));
        alert.setLocation(request.getLocation());
        alert.setMessage(request.getMessage());
        alert.setSeverity(request.getSeverity());
        alert.setStatus(Notification.AlertStatus.ACTIVE);
        alert.setTimestamp(LocalDateTime.now());
        
        Notification savedAlert = notificationRepository.save(alert);
        log.info("Alert created with ID: {}", savedAlert.getAlertId());
        
        return savedAlert;
    }
    
    public Notification resolveAlert(String alertId) {
        log.info("Resolving alert: {}", alertId);
        
        Notification alert = notificationRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        
        alert.setStatus(Notification.AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        
        return notificationRepository.save(alert);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getAllAlerts() {
        return notificationRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getActiveAlerts() {
        return notificationRepository.findByStatus(Notification.AlertStatus.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getAlertsByLocation(String location) {
        return notificationRepository.findByLocationContainingIgnoreCase(location);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getAlertsByType(String type) {
        return notificationRepository.findByType(Notification.AlertType.valueOf(type));
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getAlertsBySeverity(Integer severity) {
        return notificationRepository.findBySeverityGreaterThanEqual(severity);
    }
    
    public Notification updateAlertSeverity(String alertId, Integer severity) {
        Notification alert = notificationRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        
        alert.setSeverity(severity);
        return notificationRepository.save(alert);
    }
    
    public Notification updateAlertMessage(String alertId, String message) {
        Notification alert = notificationRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        
        alert.setMessage(message);
        return notificationRepository.save(alert);
    }
    
    public Notification updateAlertLocation(String alertId, String location) {
        Notification alert = notificationRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        
        alert.setLocation(location);
        return notificationRepository.save(alert);
    }
    
    @Transactional(readOnly = true)
    public long countActiveAlerts() {
        return notificationRepository.countByStatus(Notification.AlertStatus.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getRecentAlerts(int limit) {
        return notificationRepository.findRecentAlerts(limit);
    }
}
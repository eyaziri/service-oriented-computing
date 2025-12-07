package com.smarttourism.notification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String alertId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false, length = 1000)
    private String message;
    
    @Column(nullable = false)
    private Integer severity;  // 1-5
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    private LocalDateTime resolvedAt;
    
    // Énumérations
    public enum AlertType {
        WEATHER, SECURITY, CROWD, TRAFFIC, HEALTH, OTHER
    }
    
    public enum AlertStatus {
        ACTIVE, RESOLVED, EXPIRED
    }
    
    // Constructeur pour faciliter la création
    public Notification(AlertType type, String location, String message, Integer severity) {
        this.type = type;
        this.location = location;
        this.message = message;
        this.severity = severity;
        this.status = AlertStatus.ACTIVE;
        this.timestamp = LocalDateTime.now();
    }
}
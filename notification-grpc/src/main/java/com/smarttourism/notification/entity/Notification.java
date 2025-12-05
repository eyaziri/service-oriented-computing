package com.smarttourism.notification.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String alertId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private Integer severity; // 1-5

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertStatus status = AlertStatus.ACTIVE;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private LocalDateTime resolvedAt;

    public enum AlertType {
        WEATHER, CROWD, SECURITY, TRANSPORT, GENERAL
    }

    public enum AlertStatus {
        ACTIVE, RESOLVED, ARCHIVED
    }
}
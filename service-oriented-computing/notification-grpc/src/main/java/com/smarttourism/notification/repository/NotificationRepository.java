package com.smarttourism.notification.repository;

import com.smarttourism.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    
    List<Notification> findByStatus(Notification.AlertStatus status);
    
    List<Notification> findByLocationContainingIgnoreCase(String location);
    
    List<Notification> findByType(Notification.AlertType type);
    
    List<Notification> findBySeverityGreaterThanEqual(Integer severity);
    
    long countByStatus(Notification.AlertStatus status);
    
    @Query("SELECT n FROM Notification n ORDER BY n.timestamp DESC")
    List<Notification> findRecentAlerts(int limit);
}
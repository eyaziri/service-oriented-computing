package com.smarttourism.notification.repository;

import com.smarttourism.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Optional<Notification> findByAlertId(String alertId);
    
    List<Notification> findByLocationAndStatus(String location, Notification.AlertStatus status);
    
    List<Notification> findByTypeAndStatus(Notification.AlertType type, Notification.AlertStatus status);
    
    List<Notification> findByStatus(Notification.AlertStatus status);
    
    @Query("SELECT n FROM Notification n WHERE n.location LIKE %:location% AND n.status = 'ACTIVE'")
    List<Notification> findActiveAlertsByLocationContaining(@Param("location") String location);
    
    List<Notification> findBySeverityGreaterThanEqual(Integer severity);
    
    @Query("SELECT n FROM Notification n WHERE n.timestamp >= :since AND n.status = 'ACTIVE'")
    List<Notification> findRecentActiveAlerts(@Param("since") LocalDateTime since);
}
package com.smarttourism.orchestrator.controller;

import com.smarttourism.orchestrator.dto.AlertDTO;
import com.smarttourism.orchestrator.listener.AlertEventListener;
import com.smarttourism.orchestrator.service.NotificationClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final NotificationClientService notificationService;
    private final AlertEventListener alertEventListener;
    
    /**
     * 1. Récupérer les alertes actives via gRPC
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveAlerts(
            @RequestParam(defaultValue = "Downtown") String location) {
        
        try {
            log.info("🔍 Checking active alerts for location: {}", location);
            
            List<com.smarttourism.notification.AlertResponse> grpcAlerts = 
                notificationService.checkActiveAlerts(location);
            
            // Convertir en DTO
            List<AlertDTO> alertDTOs = new ArrayList<>();
            for (com.smarttourism.notification.AlertResponse alert : grpcAlerts) {
                AlertDTO dto = AlertDTO.builder()
                    .alertId(alert.getAlertId())
                    .type(alert.getType())
                    .location(alert.getLocation())
                    .message(alert.getMessage())
                    .severity(alert.getSeverity())
                    .timestamp(alert.getTimestamp())
                    .status(alert.getStatus())
                    .build();
                alertDTOs.add(dto);
            }
            
            log.info("✅ Retrieved {} active alerts for location: {}", 
                alertDTOs.size(), location);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("location", location);
            response.put("count", alertDTOs.size());
            response.put("alerts", alertDTOs);
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error checking active alerts for location: {}", location, e);
            
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Unable to retrieve active alerts");
            error.put("details", e.getMessage());
            error.put("location", location);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * 2. SSE Endpoint - Connexion des clients
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAlertsSse() {
        SseEmitter emitter = new SseEmitter(0L); // Timeout infini (ou 3600000L pour 1h)
        
        // Ajouter au listener
        alertEventListener.addEmitter(emitter);
        
        // Envoyer un message de bienvenue
        try {
            Map<String, Object> welcome = new HashMap<>();
            welcome.put("message", "Connected to Alert Stream");
            welcome.put("timestamp", System.currentTimeMillis());
            welcome.put("status", "connected");
            
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .name("connected")
                    .data(welcome);
            
            emitter.send(event);
            log.info("✅ SSE client connected successfully");
            
        } catch (Exception e) {
            log.error("❌ Error sending welcome message", e);
        }
        
        return emitter;
    }
    
    /**
     * 3. Endpoint pour tester SSE manuellement
     */
    @PostMapping("/broadcast-test")
    public ResponseEntity<Map<String, Object>> broadcastTestAlert() {
        log.info("🧪 Broadcasting test alert to SSE clients");
        
        // Créer une alerte de test
        com.smarttourism.notification.AlertResponse testAlert = 
            com.smarttourism.notification.AlertResponse.newBuilder()
                .setAlertId("test-" + System.currentTimeMillis())
                .setType("TEST")
                .setLocation("Test Location")
                .setMessage("🧪 This is a test alert broadcast")
                .setSeverity(3)
                .setTimestamp(String.valueOf(System.currentTimeMillis()))
                .setStatus("ACTIVE")
                .build();
        
        // Broadcaster aux clients SSE
        alertEventListener.broadcastAlert(testAlert);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Test alert broadcasted");
        response.put("connectedClients", alertEventListener.getConnectedClientsCount());
        response.put("alertId", testAlert.getAlertId());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 4. Stream avec gRPC
     */
    @GetMapping("/stream")
    public ResponseEntity<Flux<com.smarttourism.notification.AlertResponse>> streamAlerts(
            @RequestParam(defaultValue = "Downtown") String location,
            @RequestParam(required = false) List<String> types) {
        
        if (types == null || types.isEmpty()) {
            types = List.of("WEATHER", "SECURITY", "CROWD", "TRAFFIC");
        }
        
        Flux<com.smarttourism.notification.AlertResponse> stream = 
            notificationService.streamAlerts(location, types)
                .doOnNext(alert -> {
                    // Broadcaster chaque alerte reçue du stream aux clients SSE
                    alertEventListener.broadcastAlert(alert);
                });
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(stream);
    }
    
    /**
     * 5. Statistiques
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = notificationService.getStats();
        stats.put("service", "alert-orchestrator");
        stats.put("connectedClients", alertEventListener.getConnectedClientsCount());
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * 6. Test de connexion gRPC
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            // Envoyer une alerte de test via gRPC
            com.smarttourism.notification.AlertResponse response = notificationService.sendAlert(
                    "TEST", 
                    "Test Location", 
                    "Test de connexion gRPC", 
                    1);
            
            // Broadcaster aux clients SSE
            alertEventListener.broadcastAlert(response);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", "success");
            result.put("message", "Connexion gRPC fonctionnelle");
            result.put("alertId", response.getAlertId());
            result.put("timestamp", System.currentTimeMillis());
            result.put("broadcasted", true);
            result.put("sseClients", alertEventListener.getConnectedClientsCount());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Connexion gRPC échouée: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    
    /**
     * 7. Health endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "alert-orchestrator");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * 8. Info endpoint
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "alert-orchestrator");
        info.put("description", "gRPC Client for Notification Service");
        info.put("version", "1.0.0");
        info.put("connectedTo", "notification-service");
        info.put("protocol", "gRPC");
        info.put("sseEnabled", true);
        info.put("connectedSseClients", alertEventListener.getConnectedClientsCount());
        info.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(info);
    }
}
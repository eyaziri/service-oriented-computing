package com.smarttourism.orchestrator.service;

import com.smarttourism.notification.AlertRequest;
import com.smarttourism.notification.AlertResponse;
import com.smarttourism.notification.CheckRequest;
import com.smarttourism.notification.AlertListResponse;
import com.smarttourism.notification.StreamRequest;
import com.smarttourism.notification.NotificationServiceGrpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class NotificationClientService {
    
    // FIXED: Use @GrpcClient annotation instead of constructor injection
    @GrpcClient("notification-service")
    private NotificationServiceGrpc.NotificationServiceBlockingStub blockingStub;
    
    @GrpcClient("notification-service")
    private NotificationServiceGrpc.NotificationServiceStub asyncStub;
    
    private final CircuitBreakerFactory circuitBreakerFactory;
    private final List<AlertResponse> recentAlerts = new ArrayList<>();
    private final AtomicInteger alertCounter = new AtomicInteger(0);
    
    // Constructor with only CircuitBreakerFactory
    public NotificationClientService(CircuitBreakerFactory circuitBreakerFactory) {
        this.circuitBreakerFactory = circuitBreakerFactory;
    }
    
    /**
     * Méthode 1: RPC Unary - Envoi simple d'alerte
     */
    public AlertResponse sendAlert(String type, String location, String message, int severity) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("notificationService");
        
        return circuitBreaker.run(() -> {
            log.info("📤 Envoi d'alerte via gRPC (Unary): type={}, location={}", type, location);
            
            AlertRequest request = AlertRequest.newBuilder()
                    .setAlertId("alert-" + System.currentTimeMillis())
                    .setType(type)
                    .setLocation(location)
                    .setMessage(message)
                    .setSeverity(severity)
                    .build();
            
            try {
                AlertResponse response = blockingStub.sendAlert(request);
                alertCounter.incrementAndGet();
                
                log.info("✅ Alerte envoyée avec succès: id={}, type={}", 
                        response.getAlertId(), response.getType());
                
                // Ajouter aux alertes récentes
                synchronized (recentAlerts) {
                    recentAlerts.add(response);
                    if (recentAlerts.size() > 100) {
                        recentAlerts.remove(0);
                    }
                }
                
                return response;
                
            } catch (StatusRuntimeException e) {
                log.error("❌ Erreur gRPC lors de l'envoi d'alerte: {}", e.getStatus());
                throw new RuntimeException("Échec de l'appel gRPC: " + e.getStatus(), e);
            }
        }, throwable -> {
            log.error("⛔ Circuit breaker ouvert - Fallback activé");
            return AlertResponse.newBuilder()
                    .setAlertId("fallback-alert")
                    .setType(type)
                    .setLocation(location)
                    .setMessage("Service temporairement indisponible: " + message)
                    .setSeverity(severity)
                    .setTimestamp(String.valueOf(System.currentTimeMillis()))
                    .setStatus("FALLBACK")
                    .build();
        });
    }
    
    /**
     * Méthode 2: Server Streaming - Réception d'alertes en continu
     */
    public Flux<AlertResponse> streamAlerts(String location, List<String> alertTypes) {
        return Flux.create(emitter -> {
            log.info("🎯 Démarrage du streaming d'alertes pour: {}", location);
            
            StreamRequest request = StreamRequest.newBuilder()
                    .setLocation(location)
                    .addAllAlertTypes(alertTypes)
                    .build();
            
            CountDownLatch latch = new CountDownLatch(1);
            
            asyncStub.streamAlerts(request, new StreamObserver<AlertResponse>() {
                @Override
                public void onNext(AlertResponse alert) {
                    log.debug("📨 Nouvelle alerte reçue: {} - {} (severity: {})", 
                            alert.getType(), alert.getMessage(), alert.getSeverity());
                    
                    emitter.next(alert);
                    
                    // Traitement en temps réel
                    processRealTimeAlert(alert);
                    
                    // Mettre à jour les alertes récentes
                    synchronized (recentAlerts) {
                        recentAlerts.add(alert);
                        if (recentAlerts.size() > 100) {
                            recentAlerts.remove(0);
                        }
                    }
                }
                
                @Override
                public void onError(Throwable t) {
                    log.error("💥 Erreur dans le streaming: {}", t.getMessage());
                    emitter.error(t);
                    latch.countDown();
                }
                
                @Override
                public void onCompleted() {
                    log.info("✅ Streaming terminé pour: {}", location);
                    emitter.complete();
                    latch.countDown();
                }
            });
            
            // Attendre la fin du streaming
            try {
                latch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                emitter.error(e);
            }
        });
    }
    
    /**
     * Méthode 3: Flux continu pour dashboard (Reactive)
     */
    public Flux<AlertResponse> continuousStream(String location) {
        return Flux.interval(Duration.ofSeconds(5))
                .flatMap(tick -> Mono.fromCallable(() -> {
                    CheckRequest request = CheckRequest.newBuilder()
                            .setLocation(location)
                            .build();
                    
                    AlertListResponse response = blockingStub.checkActiveAlerts(request);
                    return response.getAlertsList();
                }))
                .flatMapIterable(alerts -> alerts)
                .distinct(AlertResponse::getAlertId)
                .doOnSubscribe(subscription -> 
                    log.info("🔄 Démarrage du flux continu pour: {}", location))
                .doOnTerminate(() -> 
                    log.info("⏹️  Flux continu arrêté pour: {}", location));
    }
    
    /**
     * Méthode 4: Vérification des alertes actives
     */
    public List<AlertResponse> checkActiveAlerts(String location) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("notificationService");
        
        return circuitBreaker.run(() -> {
            try {
                log.info("🔍 Checking active alerts for location: {}", location);
                
                CheckRequest request = CheckRequest.newBuilder()
                        .setLocation(location)
                        .build();
                
                AlertListResponse response = blockingStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)  // Add timeout
                    .checkActiveAlerts(request);
                    
                log.info("✅ {} active alerts found for: {}", 
                        response.getAlertsCount(), location);
                
                return response.getAlertsList();
                
            } catch (StatusRuntimeException e) {
                log.error("❌ gRPC error checking alerts: status={}, description={}", 
                    e.getStatus().getCode(), e.getStatus().getDescription());
                throw new RuntimeException("gRPC call failed: " + e.getStatus(), e);
            }
        }, throwable -> {
            log.error("⛔ Circuit breaker activated for checkActiveAlerts");
            return List.of();  // Return empty list as fallback
        });
    }
    
    /**
     * Traitement en temps réel des alertes
     */
    private void processRealTimeAlert(AlertResponse alert) {
        // Logique de traitement basée sur la sévérité
        switch (alert.getSeverity()) {
            case 5: // Urgence critique
                log.warn("🚨 ALERTE CRITIQUE: {} à {}", 
                        alert.getMessage(), alert.getLocation());
                notifyEmergencyServices(alert);
                break;
                
            case 4: // Haute priorité
                log.warn("🔴 Alerte haute priorité: {}", alert.getMessage());
                notifySecurityTeam(alert);
                break;
                
            case 3: // Moyenne
                log.warn("🟡 Alerte modérée: {}", alert.getMessage());
                notifyTouristGuides(alert);
                break;
                
            default:
                log.info("🔵 Notification: {}", alert.getMessage());
        }
    }
    
    private void notifyEmergencyServices(AlertResponse alert) {
        log.info("📞 Notification aux services d'urgence: {} à {}", 
                alert.getType(), alert.getLocation());
    }
    
    private void notifySecurityTeam(AlertResponse alert) {
        log.info("👮 Notification à l'équipe de sécurité: {}", alert.getMessage());
    }
    
    private void notifyTouristGuides(AlertResponse alert) {
        log.info("📱 Notification aux guides touristiques: {}", alert.getMessage());
    }
    
    /**
     * Vérification périodique des alertes actives
     */
    @Scheduled(fixedRate = 60000) // Toutes les minutes
    public void scheduledAlertCheck() {
        try {
            List<AlertResponse> activeAlerts = checkActiveAlerts("Downtown");
            log.info("⏰ Vérification planifiée: {} alertes actives", activeAlerts.size());
            
        } catch (Exception e) {
            log.error("Erreur lors de la vérification planifiée", e);
        }
    }
    
    /**
     * Statistiques
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAlertsSent", alertCounter.get());
        stats.put("recentAlertsCount", recentAlerts.size());
        stats.put("serviceStatus", "CONNECTED");
        stats.put("lastCheck", System.currentTimeMillis());
        
        return stats;
    }
}
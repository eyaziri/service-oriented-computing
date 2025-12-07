package com.smarttourism.orchestrator.listener;

import com.smarttourism.notification.AlertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventListener {
    
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    
    /**
     * Ajouter un nouveau client SSE
     */
    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        log.info("✅ Nouveau client SSE connecté. Total: {}", emitters.size());
        
        // Configuration des callbacks
        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
            emitters.remove(emitter);
        });
        
        emitter.onTimeout(() -> {
            log.info("SSE connection timeout");
            emitter.complete();
            emitters.remove(emitter);
        });
        
        emitter.onError(error -> {
            log.error("SSE error", error);
            emitters.remove(emitter);
        });
    }
    
    /**
     * Broadcaster une alerte à tous les clients SSE
     */
    public void broadcastAlert(AlertResponse alert) {
        log.info("📢 Broadcasting alert to {} clients: {}", emitters.size(), alert.getAlertId());
        
        List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();
        
        for (SseEmitter emitter : emitters) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("id", alert.getAlertId());
                data.put("type", alert.getType());
                data.put("location", alert.getLocation());
                data.put("message", alert.getMessage());
                data.put("severity", alert.getSeverity());
                data.put("timestamp", alert.getTimestamp());
                data.put("status", alert.getStatus());
                
                SseEmitter.SseEventBuilder event = SseEmitter.event()
                        .name("alert")
                        .id(alert.getAlertId())
                        .data(data);
                
                emitter.send(event);
                log.debug("✅ Alert sent to client");
                
            } catch (IOException e) {
                log.warn("❌ Client SSE déconnecté, marqué pour suppression");
                deadEmitters.add(emitter);
            }
        }
        
        // Nettoyer les émetteurs morts
        emitters.removeAll(deadEmitters);
        deadEmitters.forEach(emitter -> {
            try {
                emitter.complete();
            } catch (Exception e) {
                // Ignore
            }
        });
    }
    
    /**
     * Obtenir le nombre de clients connectés
     */
    public int getConnectedClientsCount() {
        return emitters.size();
    }
}
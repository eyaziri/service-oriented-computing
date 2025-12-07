package com.smarttourism.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/notification")
    public ResponseEntity<Map<String, Object>> notificationFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_TEMPORARILY_UNAVAILABLE");
        response.put("message", "Le service Notification est temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "notification-service");
        response.put("protocol", "gRPC");
        response.put("suggestion", "Veuillez réessayer dans quelques instants");
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(response);
    }
    
    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> alertsFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_TEMPORARILY_UNAVAILABLE");
        response.put("message", "Le service Alert Orchestrator est temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        response.put("service", "alert-orchestrator");
        response.put("protocol", "gRPC Client");
        response.put("suggestion", "Veuillez réessayer dans 30 secondes");
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(response);
    }
    
    @GetMapping("/attractions")
    public ResponseEntity<Map<String, Object>> attractionsFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SERVICE_TEMPORARILY_UNAVAILABLE");
        response.put("message", "Le service Attractions est temporairement indisponible");
        response.put("timestamp", System.currentTimeMillis());
        response.put("suggestion", "Veuillez réessayer dans quelques instants");
        
        return ResponseEntity
            .status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "api-gateway");
        health.put("message", "API Gateway is running but some services may be unavailable");
        health.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(health);
    }
}
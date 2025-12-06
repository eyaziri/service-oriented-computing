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
        
        return ResponseEntity.ok(health);
    }
}
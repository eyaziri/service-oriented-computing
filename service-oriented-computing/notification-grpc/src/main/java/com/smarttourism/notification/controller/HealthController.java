package com.smarttourism.notification.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notification")
public class HealthController {
    
    @Value("${spring.application.name}")
    private String appName;
    
    @Value("${server.port}")
    private String httpPort;
    
    @Value("${grpc.server.port}")
    private String grpcPort;
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", appName);
        health.put("httpPort", httpPort);
        health.put("grpcPort", grpcPort);
        health.put("timestamp", System.currentTimeMillis());
        health.put("protocols", "REST, gRPC");
        
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Notification Service");
        info.put("description", "gRPC service for real-time notifications in Smart Tourism Platform");
        info.put("version", "1.0.0");
        info.put("author", "Smart Tourism Team");
        info.put("features", "Alert sending, Stream alerts, Active alerts check");
        info.put("protocol", "gRPC with REST health endpoints");
        info.put("registeredInEureka", true);
        
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "OPERATIONAL");
        status.put("message", "Notification service is running and ready");
        status.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(status);
    }
}
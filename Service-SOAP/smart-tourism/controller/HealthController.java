package com.smart_tourism.smart_tourism.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "cultural-soap-service");
        health.put("timestamp", LocalDateTime.now().toString());
        health.put("port", 8090);
        health.put("soapEndpoint", "http://localhost:8090/ws");
        health.put("wsdl", "http://localhost:8090/ws/culturalArchive.wsdl");
        return ResponseEntity.ok(health);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Cultural Archive SOAP Service");
        info.put("description", "SOAP Web Service for cultural archives management");
        info.put("version", "1.0.0");
        info.put("technology", "Spring Boot 3 + Spring WS + JAXB");
        return ResponseEntity.ok(info);
    }
}
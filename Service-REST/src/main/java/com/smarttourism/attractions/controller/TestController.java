package com.smarttourism.attractions.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/eureka")
    public Map<String, Object> testEureka() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "✅ Attractions Service fonctionne via Eureka!");
        response.put("service", "attractions-service");
        response.put("port", 8081);
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("test", "Gateway integration test");
        
        return response;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "attractions-service");
        health.put("time", String.valueOf(System.currentTimeMillis()));
        return health;
    }
    
    @GetMapping("/simple")
    public String simple() {
        return "Hello from Attractions Service! Gateway test successful.";
    }
}
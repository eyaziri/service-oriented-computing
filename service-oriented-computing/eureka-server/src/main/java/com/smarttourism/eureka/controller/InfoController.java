package com.smarttourism.eureka.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/info")
public class InfoController {
    
    @Value("${spring.application.name}")
    private String appName;
    
    @Value("${server.port}")
    private String port;
    
    @Value("${eureka.instance.hostname:localhost}")
    private String hostname;
    
    @GetMapping
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Eureka Discovery Server");
        info.put("name", appName);
        info.put("port", port);
        info.put("hostname", hostname);
        info.put("status", "UP");
        info.put("dashboard", "http://" + hostname + ":" + port);
        info.put("actuator-health", "http://" + hostname + ":" + port + "/actuator/health");
        info.put("description", "Service Discovery for Smart Tourism Platform");
        info.put("timestamp", System.currentTimeMillis());
        
        return info;
    }
}
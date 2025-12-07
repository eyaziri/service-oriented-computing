package com.smarttourism.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class AlertOrchestratorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AlertOrchestratorApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🚨 ALERT ORCHESTRATOR (gRPC Client) DÉMARRÉ");
        System.out.println("=".repeat(60));
        System.out.println("📍 Port: 8084");
        System.out.println("📡 Eureka Server: http://localhost:8761");
        System.out.println("🔗 Service Name: alert-orchestrator");
        System.out.println("🎯 Target Service: notification-service");
        System.out.println("📊 Actuator: http://localhost:8084/actuator/health");
        System.out.println("=".repeat(60) + "\n");
    }
}
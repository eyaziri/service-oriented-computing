package com.smarttourism.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class NotificationApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🔔 NOTIFICATION SERVICE (gRPC) DÉMARRÉ");
        System.out.println("=".repeat(60));
        System.out.println("📍 HTTP Port: 8085");
        System.out.println("⚡ gRPC Port: 9090");
        System.out.println("📡 Eureka Server: http://localhost:8761");
        System.out.println("🔗 Service Name: notification-service");
        System.out.println("📊 Actuator: http://localhost:8083/actuator/health");
        System.out.println("=".repeat(60) + "\n");
    }
}
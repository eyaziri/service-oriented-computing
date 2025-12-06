package com.smarttourism.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🌉 API GATEWAY DÉMARRÉ AVEC SUCCÈS");
        System.out.println("=".repeat(60));
        System.out.println("📍 Port: 8080");
        System.out.println("🔗 Gateway URL: http://localhost:8080");
        System.out.println("📡 Eureka Server: http://localhost:8761");
        System.out.println("🏛️  Attractions Service: http://localhost:8081");
        System.out.println("\n📌 Routes disponibles:");
        System.out.println("  • http://localhost:8080/api/attractions/**");
        System.out.println("  • http://localhost:8080/actuator/gateway/routes");
        System.out.println("=".repeat(60) + "\n");
    }
}
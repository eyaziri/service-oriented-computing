package com.smarttourism.attractions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient  // ← TRÈS IMPORTANT : Active l'enregistrement Eureka
public class AttractionsServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AttractionsServiceApplication.class, args);
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🏛️  ATTRACTIONS SERVICE DÉMARRÉ");
        System.out.println("=".repeat(50));
        System.out.println("📍 Port: 8081");
        System.out.println("🔗 Swagger UI: http://localhost:8081/swagger-ui.html");
        System.out.println("📊 Actuator: http://localhost:8081/actuator/health");
        System.out.println("📡 Eureka Server: http://localhost:8761");
        System.out.println("=".repeat(50) + "\n");
    }
}
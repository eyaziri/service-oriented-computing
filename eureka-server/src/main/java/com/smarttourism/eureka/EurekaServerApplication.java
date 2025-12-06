package com.smarttourism.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Discovery Server pour Smart Tourism Platform
 * 
 * Ce serveur permet aux microservices de s'enregistrer et de se découvrir
 * automatiquement sans configuration manuelle d'URLs.
 */
@SpringBootApplication
@EnableEurekaServer  // Active le serveur Eureka
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
        
        System.out.println("\n=========================================");
        System.out.println("✅ EUREKA SERVER DÉMARRÉ AVEC SUCCÈS");
        System.out.println("=========================================");
        System.out.println("📊 Dashboard: http://localhost:8761");
        System.out.println("🔍 Actuator Health: http://localhost:8761/actuator/health");
        System.out.println("📈 Actuator Metrics: http://localhost:8761/actuator/metrics");
        System.out.println("=========================================\n");
    }
}
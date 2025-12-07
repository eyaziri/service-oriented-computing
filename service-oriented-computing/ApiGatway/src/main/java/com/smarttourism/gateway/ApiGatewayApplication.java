package com.smarttourism.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);

        String separator = "=".repeat(60);

        System.out.println("\n" + separator);
        System.out.println("🌉 API GATEWAY DÉMARRÉ AVEC SUCCÈS");
        System.out.println(separator);

        System.out.println("📍 Port: 8080");
        System.out.println("🔗 Gateway URL: http://localhost:8080");
        System.out.println("📡 Eureka Server: http://localhost:8761");
        System.out.println("🏛️ Attractions Service: http://localhost:8081");
        System.out.println("🎭 Cultural SOAP Service: http://localhost:8090");
        System.out.println("🚀 GraphQL Service: http://localhost:4000");

        System.out.println("\n📌 Routes disponibles:");
        System.out.println("  • REST Attractions: http://localhost:8080/api/attractions/**");
        System.out.println("  • Swagger UI Attractions: http://localhost:8080/api/attractions-service/swagger-ui.html");
        System.out.println("  • API Docs Attractions: http://localhost:8080/api/attractions-service/api-docs");
        System.out.println("  • SOAP WSDL: http://localhost:8080/soap/wsdl/**");
        System.out.println("  • SOAP Endpoint: http://localhost:8080/soap/ws/**");
        System.out.println("  • GraphQL Endpoint: http://localhost:8080/graphql");
        System.out.println("  • Apollo Sandbox: http://localhost:8080/sandbox");
        System.out.println("  • Actuator Routes: http://localhost:8080/actuator/gateway/routes");
        System.out.println("  • Health Check: http://localhost:8080/actuator/health");

        System.out.println(separator + "\n");
    }
}

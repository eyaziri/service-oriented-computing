package com.smart_tourism.smart_tourism.soap.config;

import com.smart_tourism.smart_tourism.soap.service.CulturalArchiveServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(CulturalArchiveServiceImpl service) {
        return args -> {
            System.out.println("🔄 Vérification et initialisation des données...");
            service.initializeSampleData();
            System.out.println("✅ Base de données prête!");
        };
    }
}

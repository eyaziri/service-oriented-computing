package com.smarttourism.attractions.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EurekaConfig {
    
    @Bean
    @LoadBalanced  // Permet d'utiliser le nom du service au lieu de l'URL
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
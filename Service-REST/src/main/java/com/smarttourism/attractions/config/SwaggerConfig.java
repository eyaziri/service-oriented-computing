package com.smarttourism.attractions.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI smartTourismOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8081");
        devServer.setDescription("Serveur de développement local");
        
        Server prodServer = new Server();
        prodServer.setUrl("https://api.smarttourism.com");
        prodServer.setDescription("Serveur de production");
        
        Contact contact = new Contact();
        contact.setName("Équipe Smart Tourism");
        contact.setEmail("contact@smarttourism.com");
        contact.setUrl("https://www.smarttourism.com");
        
        License mitLicense = new License()
            .name("MIT License")
            .url("https://opensource.org/licenses/MIT");
        
        Info info = new Info()
            .title("Smart Tourism - Attractions Service API")
            .version("1.0.0")
            .contact(contact)
            .description("API REST pour la gestion des attractions touristiques")
            .license(mitLicense);
        
        return new OpenAPI()
            .info(info)
            .servers(List.of(devServer));
    }
}
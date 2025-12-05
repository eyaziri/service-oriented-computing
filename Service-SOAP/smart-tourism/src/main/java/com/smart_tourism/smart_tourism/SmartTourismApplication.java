package com.smart_tourism.smart_tourism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartTourismApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartTourismApplication.class, args);
		System.out.println("✅ Service SOAP des archives culturelles démarré !");
		System.out.println("📡 WSDL disponible à : http://localhost:8080/ws/culturalArchive.wsdl");
		System.out.println("🔧 Endpoint SOAP : http://localhost:8080/ws");
	}
}

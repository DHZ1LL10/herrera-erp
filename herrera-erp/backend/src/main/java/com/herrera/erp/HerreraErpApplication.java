package com.herrera.erp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase Principal de Herrera ERP
 * Ubicación: backend/src/main/java/com/herrera/erp/HerreraErpApplication.java
 */
@SpringBootApplication
public class HerreraErpApplication {

    public static void main(String[] args) {
        System.out.println("===========================================");
        System.out.println("  HERRERA ERP - Sistema de Gestión");
        System.out.println("  Deportes Herrera - MVP Local");
        System.out.println("  Puerto: 8080");
        System.out.println("===========================================");

        SpringApplication.run(HerreraErpApplication.class, args);
    }

    /**
     * Configuración de CORS para permitir requests desde el frontend
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                                "http://localhost:5173", // Vite
                                "http://localhost:3000", // Create React App
                                "http://192.168.1.*" // Red local
                )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
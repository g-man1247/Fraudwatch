package com.airquality.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS Configuration for the Air Quality Monitoring API.
 *
 * Allows the future frontend (HTML/JavaScript) running on localhost
 * to communicate with this Spring Boot backend during development.
 *
 * Permitted origins:
 *   - http://localhost:3000  (React / Node dev server)
 *   - http://localhost:5500  (VS Code Live Server)
 *   - http://localhost:8080  (same server)
 *   - http://127.0.0.1:5500 (Live Server alternative)
 *
 * When deploying to production, update these origins
 * to match the actual frontend domain.
 */
@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        // Allow all localhost development origins
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://localhost:5500",
                                "http://localhost:8080",
                                "http://127.0.0.1:5500",
                                "http://localhost:5173"
                        )
                        // Allow standard HTTP methods
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // Allow all headers (needed for Content-Type, etc.)
                        .allowedHeaders("*")
                        // Allow cookies/session if needed later
                        .allowCredentials(true);
            }
        };
    }
}

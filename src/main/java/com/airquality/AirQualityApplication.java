package com.airquality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Urban Air Quality Monitoring &
 * Industrial Emissions Fine Management Hub.
 *
 * Run with:
 *   mvn spring-boot:run
 * or directly from your IDE.
 */
@SpringBootApplication
public class AirQualityApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirQualityApplication.class, args);
    }
}

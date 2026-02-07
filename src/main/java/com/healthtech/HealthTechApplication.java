package com.healthtech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the HealthTech Demo Application.
 * 
 * This application demonstrates:
 * - Spring Boot with layered architecture (Controller -> Service -> Repository)
 * - Spring Security with JWT authentication
 * - Spring Data JPA with PostgreSQL
 * - Concurrency handling for appointment booking
 * - RESTful API design following SOLID principles
 * 
 * @author HealthTech Demo
 * @version 1.0.0
 */
@SpringBootApplication
public class HealthTechApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthTechApplication.class, args);
    }
}

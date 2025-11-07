package com.splitia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.splitia.repository")
public class DatabaseConfig {
    // Database configuration is handled by Spring Boot auto-configuration
    // Additional custom configurations can be added here if needed
}


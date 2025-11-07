package com.splitia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Web configuration
    // CORS is handled in SecurityConfig
    // Additional web configurations can be added here
}


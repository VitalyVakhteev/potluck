package com.picnic.potluck.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // apply to all endpoints
                        .allowedOrigins("*")  // allow all origins for now todo: restrict later
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")  // I've never used options Todo: might be a good time to learn options...
                        .allowedHeaders("*");
            }
        };
    }
}

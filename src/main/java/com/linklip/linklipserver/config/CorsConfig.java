package com.linklip.linklipserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://develop.d2q3btuxg9ucoe.amplifyapp.com/",
                        "http://develop.d2q3btuxg9ucoe.amplifyapp.com/",
                        "http://localhost:3000/",
                        "http://localhost:8080/")
                .allowCredentials(true);
    }
}

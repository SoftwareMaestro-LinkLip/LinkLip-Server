package com.linklip.linklipserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class LinklipserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinklipserverApplication.class, args);
    }

    @Configuration
    public class WebConfig implements WebMvcConfigurer {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins(
                            "https://develop.d2q3btuxg9ucoe.amplifyapp.com/",
                            "http://develop.d2q3btuxg9ucoe.amplifyapp.com/");
        }
    }
}

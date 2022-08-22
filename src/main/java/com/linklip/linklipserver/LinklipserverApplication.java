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
}

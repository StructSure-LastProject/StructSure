package fr.uge.structsure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// DELETE THIS MAYBE AFTER IF WE USE EMBEDDED DATABASE, BUT I DONT REALLY KNOW

@Configuration
public class DevCorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/structures/**").allowedOrigins("http://localhost:5173");
    }
}
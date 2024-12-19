package fr.uge.structsure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Value("${app.allowedOrigin}")
    private String allowedOrigin;

    /**
     * This config file defines the allowed origins that will call our Spring Boot APIs, so
     * it will allow the processing of calls from our front-end.
     * @return CorsFilter the bean definition
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(allowedOrigin); // Allow your frontend origin
        corsConfiguration.addAllowedHeader("*"); // Allow all headers
        corsConfiguration.addAllowedMethod("*"); // Allow all HTTP methods
        corsConfiguration.setAllowCredentials(true); // Allow credentials like cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // Apply to all routes
        return new CorsFilter(source);
    }
}
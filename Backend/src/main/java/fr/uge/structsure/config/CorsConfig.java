package fr.uge.structsure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    private static final String ALLOWED_ORIGIN = getDomainName();

    /**
     * This config file defines the allowed origins that will call our Spring Boot APIs, so
     * it will allow the processing of calls from our front-end.
     * @return CorsFilter the bean definition
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(ALLOWED_ORIGIN); // Allow your frontend origin
        corsConfiguration.addAllowedHeader("*"); // Allow all headers
        corsConfiguration.addAllowedMethod("*"); // Allow all HTTP methods
        corsConfiguration.setAllowCredentials(true); // Allow credentials like cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // Apply to all routes
        return new CorsFilter(source);
    }

    /**
     * Gets the domain from the environment variable "DOMAIN_NAME" and
     * makes sure that the value is present.
     * @return the domaine name
     */
    private static String getDomainName() {
        var origin = System.getenv("DOMAIN_NAME");
        if (origin == null) {
            // TODO Log!
            System.err.println("Environment variable 'DOMAIN_NAME' must be defined to run in production");
            origin = "*"; // default value that works with localhost
        }
        return origin;
    }
}
package fr.uge.structsure.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Objects;

/**
 * Configuration class for security rules such as requiring authentication
 * for specific endpoints.
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final JwtUtils jwtUtils;

    private final CustomUserDetailsService customUserDetailsService;
    private final RoleFilter roleFilter;

    /**
     * Internal constructor intended to be used by Spring only to set
     * autowired fields.
     * @param jwtUtils JsonWebToken manager
     * @param customUserDetailsService to load user by name
     * @param roleFilter to restrict endpoint access by role
     */
    @Autowired
    public SpringSecurityConfig(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService, RoleFilter roleFilter) {
        this.jwtUtils = Objects.requireNonNull(jwtUtils);
        this.customUserDetailsService = Objects.requireNonNull(customUserDetailsService);
        this.roleFilter = roleFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    /**
     * General configuration of the Spring's security rules.
     * @param http object used to set the security configuration
     * @return the built security from http
     * @throws Exception if an error occurs
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var jwtFilter = new JwtFilter(customUserDetailsService, jwtUtils);
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> {
                authorize.requestMatchers("/api/login").permitAll();
                authorize.requestMatchers("/api/android/login").permitAll();
                authorize.requestMatchers("/api/register").permitAll();
                authorize.requestMatchers("/swagger-ui/index.html").permitAll();
                authorize.requestMatchers("/api/*").authenticated();
                authorize.anyRequest().permitAll();
            })
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                // Returns 401 error code when not authenticated 
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(roleFilter, JwtFilter.class)
            .formLogin(form -> form.loginPage("/login"));
        return http.build();
    }

}

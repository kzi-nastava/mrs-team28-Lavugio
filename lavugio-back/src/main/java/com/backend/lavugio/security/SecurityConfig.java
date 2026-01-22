package com.backend.lavugio.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure BCryptPasswordEncoder bean
     * Uses strength 12 (4^12 iterations) - balance between security and performance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configure HTTP security
     * - Allow /register and /login without authentication
     * - Allow WebSocket connections (for chat feature)
     * - Disable CSRF (since we're using stateless JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints (no authentication required)
                        .requestMatchers("/api/regularUsers/register").permitAll()
                        .requestMatchers("/api/regularUsers/login").permitAll()
                        .requestMatchers("/api/regularUsers/verify-email").permitAll()
                        .requestMatchers("/api/drivers/register").permitAll()
                        .requestMatchers("/api/drivers/login").permitAll()
                        .requestMatchers("/api/administrators/register").permitAll()
                        .requestMatchers("/api/administrators/login").permitAll()
                        .requestMatchers("/api/favorite-routes/**").permitAll()
                        .requestMatchers("/api/rides/estimate-price").permitAll()
                        // WebSocket for chat (allow all)
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}

package com.taxdividend.backend.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Security configuration for internal backend services.
 *
 * This backend is designed to be called ONLY by the BFF Gateway, not directly
 * by clients.
 * Authentication is handled via the X-User-Id header passed from the BFF.
 *
 * Public endpoints:
 * - /actuator/health/** - Health checks for monitoring (Kubernetes, Docker,
 * etc.)
 * - /actuator/prometheus - Prometheus metrics scraping
 * - /swagger-ui/** - API documentation (consider restricting in production)
 * - /v3/api-docs/** - OpenAPI schema
 *
 * All other endpoints require X-User-Id header.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class InternalSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (stateless API with header-based auth)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session (no cookies)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public health checks - NO authentication required
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()

                        // Public Prometheus metrics - NO authentication required (secured by network)
                        .requestMatchers("/actuator/prometheus").permitAll()

                        // All other actuator endpoints require authentication
                        .requestMatchers("/actuator/**").authenticated()

                        // API Documentation - Public (consider restricting in production)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Error handling
                        .requestMatchers("/error").permitAll()

                        // All other requests require authentication (X-User-Id header)
                        .anyRequest().authenticated())

                // Add custom filter to validate X-User-Id header
                .addFilterBefore(new InternalSecurityFilter(), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Custom filter to validate X-User-Id header from BFF Gateway.
     *
     * This filter runs for all requests EXCEPT public endpoints (actuator health,
     * prometheus, swagger).
     * It extracts the X-User-Id header and creates a Spring Security authentication
     * token.
     */
    @Slf4j
    public static class InternalSecurityFilter extends OncePerRequestFilter {

        private final com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            String requestUri = request.getRequestURI();

            // Skip authentication for public endpoints
            if (isPublicEndpoint(requestUri)) {
                log.debug("Public endpoint accessed: {}", requestUri);
                filterChain.doFilter(request, response);
                return;
            }

            // Extract X-User-Context header (set by BFF Gateway)
            String contextHeader = request.getHeader("X-User-Context");
            if (contextHeader == null || contextHeader.isEmpty()) {
                log.warn("Missing X-User-Context header for protected endpoint: {}", requestUri);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                        "Missing X-User-Context header. This service must be called through the BFF Gateway.");
                return;
            }

            try {
                // Decode Base64 and parse JSON
                byte[] decodedBytes = java.util.Base64.getDecoder().decode(contextHeader);
                com.taxdividend.backend.security.UserContext userContext = objectMapper.readValue(decodedBytes,
                        com.taxdividend.backend.security.UserContext.class);

                log.debug("Authenticated request for user: {} ({}) on endpoint: {}", userContext.userId(),
                        userContext.email(), requestUri);

                // Map roles to authorities
                java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = java.util.Collections
                        .emptyList();
                if (userContext.roles() != null) {
                    authorities = userContext.roles().stream()
                            .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                            .collect(java.util.stream.Collectors.toList());
                }

                // Create authentication token
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userContext.userId(), userContext, authorities);

                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                log.error("Failed to parse UserContext", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid UserContext");
            }
        }

        /**
         * Check if the request URI is a public endpoint that doesn't require
         * authentication.
         */
        private boolean isPublicEndpoint(String uri) {
            return uri.startsWith("/actuator/health") ||
                    uri.equals("/actuator/prometheus") ||
                    uri.startsWith("/swagger-ui") ||
                    uri.startsWith("/v3/api-docs") ||
                    uri.equals("/error");
        }
    }
}

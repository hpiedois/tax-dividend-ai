package com.taxdividend.backend.config;

import com.taxdividend.backend.security.InternalApiKeyFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for internal backend services.
 *
 * Architecture:
 * - Frontend → Keycloak (validates credentials, issues JWT)
 * - Frontend → BFF Gateway (sends JWT in Authorization header)
 * - BFF Gateway validates JWT from Keycloak
 * - BFF Gateway → Backend (sends X-Internal-Api-Key + X-User-Context headers)
 * - Backend trusts BFF Gateway (internal network only)
 *
 * Security layers:
 * 1. X-Internal-Api-Key: Validates request comes from BFF (not external attacker)
 * 2. X-User-Context: Contains user info extracted from validated JWT by BFF
 *
 * Public endpoints (no authentication required):
 * - /actuator/health/** - Health checks for Kubernetes/monitoring
 * - /actuator/prometheus - Prometheus metrics scraping
 * - /swagger-ui/** - API documentation (consider restricting in production)
 * - /v3/api-docs/** - OpenAPI schema
 * - /error - Error handling
 *
 * All other endpoints require both headers.
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class InternalSecurityConfig {

    private final InternalApiKeyFilter internalApiKeyFilter;

    @Value("${management.security.username:admin}")
    private String actuatorUsername;

    @Value("${management.security.password:changeme}")
    private String actuatorPassword;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Disable CSRF (stateless API with header-based auth)
                .csrf(AbstractHttpConfigurer::disable)

                // Stateless session (no cookies, no session state)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public health checks - NO authentication required (Kubernetes probes)
                        .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()

                        // Public Prometheus metrics - NO authentication required (secured by network)
                        .requestMatchers("/actuator/prometheus").permitAll()

                        // All other actuator endpoints require Basic Auth
                        .requestMatchers("/actuator/**").hasRole("ACTUATOR")

                        // API Documentation - Public (consider restricting in production)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Error handling
                        .requestMatchers("/error").permitAll()

                        // All other requests require authentication (X-Internal-Api-Key + X-User-Context)
                        .anyRequest().authenticated())

                // Enable HTTP Basic Auth for actuator endpoints
                .httpBasic(httpBasic -> {})

                // Add custom filter to validate X-Internal-Api-Key and X-User-Context headers
                .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * User details for actuator endpoints Basic Auth.
     * Credentials are loaded from environment variables.
     */
    @Bean
    public UserDetailsService actuatorUserDetailsService() {
        UserDetails actuatorUser = User.builder()
            .username(actuatorUsername)
            .password(passwordEncoder().encode(actuatorPassword))
            .roles("ACTUATOR")
            .build();

        log.info("Actuator Basic Auth configured for user: {}", actuatorUsername);
        return new InMemoryUserDetailsManager(actuatorUser);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

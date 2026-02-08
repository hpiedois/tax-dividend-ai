package com.taxdividend.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taxdividend.backend.security.InternalApiKeyFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.ArgumentMatchers.any;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    // Mock the InternalApiKeyFilter to disable it in tests
    // Using @Bean @Primary to ensure it replaces the real bean and allows us to
    // stub behavior
    @Bean
    @Primary
    public InternalApiKeyFilter internalApiKeyFilter() {
        // We need a real filter implementation for tests to parse X-User-Context
        // but we want to skip the API Key check and UserService lookup
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new InternalApiKeyFilter(objectMapper, null) {
            @Override
            protected void doFilterInternal(jakarta.servlet.http.HttpServletRequest request,
                    jakarta.servlet.http.HttpServletResponse response,
                    FilterChain filterChain) throws jakarta.servlet.ServletException, java.io.IOException {

                String userContextB64 = request.getHeader("X-User-Context");

                if (userContextB64 != null && !userContextB64.isBlank()) {
                    try {
                        byte[] decodedBytes = java.util.Base64.getDecoder().decode(userContextB64);
                        com.taxdividend.backend.security.UserContext userContext = objectMapper
                                .readValue(decodedBytes, com.taxdividend.backend.security.UserContext.class);

                        com.taxdividend.backend.security.UserContextHolder.set(userContext);

                        // Also set SecurityContext for Spring Security
                        if (userContext.userId() != null) {
                            java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = java.util.Collections
                                    .emptyList();
                            if (userContext.roles() != null) {
                                authorities = java.util.Arrays.stream(userContext.roles())
                                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                                        .collect(java.util.stream.Collectors.toList());
                            }

                            org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                                    userContext.userId(),
                                    userContext,
                                    authorities);
                            org.springframework.security.core.context.SecurityContextHolder.getContext()
                                    .setAuthentication(auth);
                        }
                    } catch (Exception e) {
                        // Ignore parsing errors in tests, or log them
                        System.err.println("Failed to parse X-User-Context in test: " + e.getMessage());
                    }
                }

                try {
                    filterChain.doFilter(request, response);
                } finally {
                    com.taxdividend.backend.security.UserContextHolder.clear();
                    org.springframework.security.core.context.SecurityContextHolder.clearContext();
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(internalApiKeyFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        return new ObjectMapper();
    }
}

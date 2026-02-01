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
        InternalApiKeyFilter mockFilter = mock(InternalApiKeyFilter.class);
        try {
            // Important: The mock must call chain.doFilter to allow the request to proceed
            // to the controller
            doAnswer(invocation -> {
                ServletRequest request = invocation.getArgument(0);
                ServletResponse response = invocation.getArgument(1);
                FilterChain chain = invocation.getArgument(2);
                chain.doFilter(request, response);
                return null;
            }).when(mockFilter).doFilter(any(), any(), any());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mockFilter;
    }

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    @Primary
    public ObjectMapper testObjectMapper() {
        return new ObjectMapper();
    }
}

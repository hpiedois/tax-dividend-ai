package com.taxdividend.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Security filter that validates internal API requests from BFF Gateway.
 *
 * Security checks:
 * 1. Validates X-Internal-Api-Key header matches configured secret
 * 2. Extracts and validates X-User-Context header (Base64-encoded JSON with userId, email, roles)
 * 3. Stores user context in both SecurityContextHolder and UserContextHolder
 *
 * Architecture:
 * - Frontend → Keycloak (validates credentials, issues JWT)
 * - Frontend → BFF Gateway (sends JWT in Authorization header)
 * - BFF Gateway validates JWT from Keycloak
 * - BFF Gateway → Backend (sends X-Internal-Api-Key + X-User-Context in Base64)
 * - Backend trusts BFF Gateway (internal network only)
 *
 * Public endpoints (no authentication required):
 * - /actuator/health/** - Health checks for Kubernetes/monitoring
 * - /actuator/prometheus - Metrics scraping
 * - /swagger-ui/** - API documentation
 * - /v3/api-docs/** - OpenAPI schema
 * - /error - Error handling
 */
@Component
@Slf4j
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String INTERNAL_API_KEY_HEADER = "X-Internal-Api-Key";
    private static final String USER_CONTEXT_HEADER = "X-User-Context";

    @Value("${app.security.internal-api-key}")
    private String expectedApiKey;

    private final ObjectMapper objectMapper;
    private final com.taxdividend.backend.service.UserService userService;

    /**
     * Constructor with dependencies injection.
     * In Spring Boot 4, ObjectMapper must be explicitly configured as a bean.
     * @see com.taxdividend.backend.config.JacksonConfig
     */
    public InternalApiKeyFilter(ObjectMapper objectMapper, com.taxdividend.backend.service.UserService userService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint accessed: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1. Validate X-Internal-Api-Key (ensures request comes from BFF)
            String apiKey = request.getHeader(INTERNAL_API_KEY_HEADER);
            if (apiKey == null || apiKey.isBlank()) {
                log.warn("Missing {} header from {} for {}", INTERNAL_API_KEY_HEADER, request.getRemoteAddr(), path);
                sendUnauthorized(response, "Missing internal API key. This service must be called through the BFF Gateway.");
                return;
            }

            if (!expectedApiKey.equals(apiKey)) {
                log.error("Invalid {} from {} for {}", INTERNAL_API_KEY_HEADER, request.getRemoteAddr(), path);
                sendUnauthorized(response, "Invalid internal API key");
                return;
            }

            // 2. Extract and parse X-User-Context (Base64-encoded JSON)
            String userContextB64 = request.getHeader(USER_CONTEXT_HEADER);
            if (userContextB64 == null || userContextB64.isBlank()) {
                log.warn("Missing {} header from {} for {}", USER_CONTEXT_HEADER, request.getRemoteAddr(), path);
                sendUnauthorized(response, "Missing user context");
                return;
            }

            UserContext userContext;
            try {
                // Decode Base64 and parse JSON
                byte[] decodedBytes = Base64.getDecoder().decode(userContextB64);
                userContext = objectMapper.readValue(decodedBytes, UserContext.class);
            } catch (Exception e) {
                log.error("Failed to parse {} header: {}", USER_CONTEXT_HEADER, e.getMessage());
                sendUnauthorized(response, "Invalid user context format");
                return;
            }

            if (userContext.userId() == null) {
                log.error("User context missing userId");
                sendUnauthorized(response, "Invalid user context: missing userId");
                return;
            }

            // 3. Auto-provision SSO users or validate classic users exist
            try {
                userService.findOrCreateFromSso(userContext);
            } catch (com.taxdividend.backend.service.UserService.UserNotRegisteredException e) {
                log.warn("User not registered: {}", e.getMessage());
                sendUnauthorized(response, e.getMessage());
                return;
            }

            // 4. Store user context in thread-local (for service layer access)
            UserContextHolder.set(userContext);

            // 5. Create Spring Security authentication (for @PreAuthorize, etc.)
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            if (userContext.roles() != null) {
                authorities = Arrays.stream(userContext.roles())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            }

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userContext.userId(),
                userContext,
                authorities
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.debug("Authenticated request for user {} ({}) on endpoint {}",
                userContext.userId(), userContext.email(), path);

            // 6. Continue filter chain
            filterChain.doFilter(request, response);

        } finally {
            // Always clear contexts after request to prevent memory leaks
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Check if the request URI is a public endpoint that doesn't require authentication.
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.startsWith("/actuator/health") ||
               uri.equals("/actuator/prometheus") ||
               uri.startsWith("/swagger-ui") ||
               uri.startsWith("/v3/api-docs") ||
               uri.equals("/error");
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"error\":\"Unauthorized\",\"message\":\"%s\"}",
            message
        ));
    }
}


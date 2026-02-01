package com.taxdividend.backend.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keycloak Admin Client configuration.
 *
 * This configuration creates a Keycloak admin client that can:
 * - Create new users in Keycloak realm
 * - Manage user attributes and roles
 * - Send verification emails
 * - Reset passwords
 *
 * The admin client authenticates with Keycloak using admin credentials.
 */
@Slf4j
@Configuration
public class KeycloakConfig {

    @Value("${app.keycloak.server-url}")
    private String serverUrl;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Value("${app.keycloak.admin-username}")
    private String adminUsername;

    @Value("${app.keycloak.admin-password}")
    private String adminPassword;

    @Value("${app.keycloak.client-id}")
    private String clientId;

    @PostConstruct
    public void logConfig() {
        log.info("Keycloak configuration:");
        log.info("  Server URL: {}", serverUrl);
        log.info("  Realm: {}", realm);
        log.info("  Admin User: {}", adminUsername);
        log.info("  Client ID: {}", clientId);
    }

    /**
     * Creates Keycloak admin client bean.
     *
     * This client authenticates using admin credentials and can perform
     * administrative operations on the Keycloak realm.
     *
     * @return Keycloak admin client
     */
    @Bean
    public Keycloak keycloakAdminClient() {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")  // Admin client always connects to master realm
            .username(adminUsername)
            .password(adminPassword)
            .clientId("admin-cli")  // Built-in admin CLI client
            .build();
    }

    /**
     * Returns the configured realm name for user operations.
     */
    @Bean(name = "keycloakRealmName")
    public String keycloakRealmName() {
        return realm;
    }
}

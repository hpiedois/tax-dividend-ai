package com.taxdividend.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration using Caffeine.
 *
 * Caches:
 * - taxRules: Tax treaty rules (TTL: 1 hour, max 1000 entries)
 *   Used in TaxCalculationService to avoid repeated database queries
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    public static final String TAX_RULES_CACHE = "taxRules";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(TAX_RULES_CACHE);
        cacheManager.setCaffeine(caffeineCacheBuilder());

        log.info("Caffeine cache manager initialized with cache: {}", TAX_RULES_CACHE);
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)  // TTL: 1 hour
                .maximumSize(1000)  // Max 1000 entries
                .recordStats()  // Enable statistics for monitoring
                .evictionListener((key, value, cause) ->
                    log.debug("Cache eviction - key: {}, cause: {}", key, cause));
    }
}

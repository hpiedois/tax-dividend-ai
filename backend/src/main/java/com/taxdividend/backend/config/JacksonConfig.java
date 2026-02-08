package com.taxdividend.backend.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.TimeZone;

/**
 * Jackson configuration for Spring Boot 4.
 *
 * In Spring Boot 4, ObjectMapper is no longer a global infrastructure bean.
 * Each subsystem (MVC, WebFlux, Security, Actuator) has its own internal mapper.
 *
 * This configuration provides a production-ready ObjectMapper for financial applications
 * with proper modules, precision handling, and timezone management.
 *
 * @see <a href="https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide#jackson">Spring Boot 4 Migration Guide</a>
 */
@Configuration
public class JacksonConfig {

    /**
     * Application ObjectMapper bean.
     *
     * Configured for financial/tax domain with:
     * - BigDecimal precision for monetary amounts (critical for tax calculations)
     * - UTC timezone for consistency across regions
     * - Java 8+ type support (Optional, Stream, LocalDate, etc.)
     * - Enum flexibility (case-insensitive, toString-based)
     * - Hibernate/JPA compatibility
     * - Stable property ordering for reproducibility
     *
     * @return Configured ObjectMapper for application use
     */
    @Bean
    @Primary
    public ObjectMapper applicationObjectMapper() {
        ObjectMapper mapper = JsonMapper.builder()
                // Essential modules
                .addModule(new JavaTimeModule())        // LocalDate, LocalDateTime, etc.
                .addModule(new Jdk8Module())            // Optional, Stream, etc.
                .addModule(new ParameterNamesModule())  // Records, constructor params

                // Dates as ISO-8601 (not timestamps)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

                // BigDecimal precision (CRITICAL for financial calculations)
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)

                // Modern REST API tolerance (allow unknown properties for API evolution)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

                // Enum handling (more flexible)
                .enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING)
                .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)

                // Cleaner JSON (omit nulls)
                .serializationInclusion(JsonInclude.Include.NON_NULL)

                // Hibernate/JPA proxy support (common with Spring Data JPA)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

                // Stable ordering for tests, logs, signatures
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

                .build();

        // UTC timezone (critical for financial APIs across regions)
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Case-insensitive enum matching (better API UX)
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);

        return mapper;
    }
}





package com.unisa.seproject.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit Jackson configuration.
 *
 * <p>Registers the {@link JavaTimeModule} so that Java 8+ date/time types
 * ({@code LocalTime}, {@code LocalDate}, {@code LocalDateTime}, {@code DayOfWeek}, …)
 * are serialised as ISO-8601 strings instead of numeric timestamps.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}

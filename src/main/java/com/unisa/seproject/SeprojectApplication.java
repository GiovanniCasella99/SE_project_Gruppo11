package com.unisa.seproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot entry point.
 *
 * <p>Placed at the root package {@code com.unisa.seproject} so that
 * {@code @SpringBootApplication}'s component scan covers all subpackages automatically,
 * and Spring Boot Test can discover this class without explicit configuration.
 */
@SpringBootApplication
@EnableScheduling
public class SeprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeprojectApplication.class, args);
    }
}

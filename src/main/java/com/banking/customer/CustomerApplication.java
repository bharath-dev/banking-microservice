package com.banking.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for our Banking Customer Microservice.
 *
 * @SpringBootApplication is a convenience annotation that combines:
 * 1. @Configuration: Tags the class as a source of bean definitions.
 * 2. @EnableAutoConfiguration: Automatically configures Spring based on jar dependencies (e.g. Tomcat, H2).
 * 3. @ComponentScan: Scans for @Component, @Service, @Repository, and @RestController in this package.
 */
@SpringBootApplication
public class CustomerApplication {

    // The standard Java main method - where execution begins
    public static void main(String[] args) {
        // Boots up Spring, starts embedded Tomcat on port 8080, and initializes beans
        SpringApplication.run(CustomerApplication.class, args);
    }

}
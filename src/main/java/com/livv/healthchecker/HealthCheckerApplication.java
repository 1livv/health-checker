package com.livv.healthchecker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class HealthCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthCheckerApplication.class, args);
    }

}

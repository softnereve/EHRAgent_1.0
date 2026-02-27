package com.softnerve.epic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.softnerve.epic.repo")
public class EHRAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(EHRAgentApplication.class, args);
    }
}

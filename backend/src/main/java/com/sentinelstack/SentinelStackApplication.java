package com.sentinelstack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SentinelStackApplication {

    public static void main(String[] args) {
        SpringApplication.run(SentinelStackApplication.class, args);
    }
}

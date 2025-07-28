package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @GetMapping("/fallback/user-service")
    public Mono<String> userServiceFallback() {
        return Mono.just("User service is taking too long to respond or is down. Please try again later");
    }

    @GetMapping("/fallback/notification-service")
    public Mono<String> notificationServiceFallback() {
        return Mono.just("Notification service is taking too long to respond or is down. Please try again later");
    }
}
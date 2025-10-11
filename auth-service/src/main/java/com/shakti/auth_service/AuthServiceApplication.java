package com.shakti.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling

// scanning the common-libs also, to register the RedisService into auth-service
// via Spring IOC
@SpringBootApplication(scanBasePackages = {
    "com.shakti.auth_service",
    "com.shakti.microservices.common_libs.Redis"
})
public class AuthServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}

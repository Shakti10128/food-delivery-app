package com.shakti.auth_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@EnableWebSecurity
public class AppConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // disable CSRF for REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/signup", "/auth/signin").permitAll() // public routes
                .anyRequest().authenticated()) // everything else requires auth
            .formLogin(AbstractHttpConfigurer::disable) // disable login page
            .httpBasic(AbstractHttpConfigurer::disable); // disable default basic auth

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

}

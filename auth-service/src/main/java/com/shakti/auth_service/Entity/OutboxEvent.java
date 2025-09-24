package com.shakti.auth_service.Entity;

import java.time.LocalDateTime;

import com.shakti.microservices.common_libs.Enums.EventType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;   // e.g. "USER_SIGNUP"
    @Lob                        // tells JPA to store this as CLOB
    private String payload;     // JSON string of SignupResponseDto
    private boolean published = false;
    private LocalDateTime createdAt = LocalDateTime.now();
}

package com.shakti.auth_service.Entity;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.shakti.microservices.common_libs.Enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Username can't be empty")
    private String username;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email can't be empty")
    @Email(message = "Please provide a valid email")
    private String email;

    @Column(nullable = false)
    @Size(min = 5, message = "Password should be at least 5 characters long")
    private String password;

    @NotNull(message = "Please specify your role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private Boolean Active;

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}

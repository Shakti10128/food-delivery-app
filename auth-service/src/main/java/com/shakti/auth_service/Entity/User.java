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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    @NotBlank(message = "username can't be empty")
    @NotNull
    private String username;

    @Column(nullable = false,unique = true)
    @NotBlank(message = "email can't be empty")
    @NotNull
    @Email(message = "Please provide a valid email")
    private String email;


    @Column(nullable = false)
    @Min(value = 5,message = "password should be atleast of 5 char")
    private String password;

    @NotBlank(message = "Please specify your role")
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
}

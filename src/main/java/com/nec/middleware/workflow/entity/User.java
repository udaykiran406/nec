package com.nec.middleware.workflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_username", columnList = "username"),
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_role", columnList = "role")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            unique = true,
            length = 100)
    private String username;

    @Column(
            name = "full_name",
            nullable = false,
            length = 200)
    private String fullName;

    @Column(
            name = "email",
            nullable = false,
            unique = true,
            length = 255)
    private String email;

    @Column(
            name = "role",
            nullable = false,
            length = 100)
    private String role;

    @Column(name = "is_active")
    private Boolean isActive;
}
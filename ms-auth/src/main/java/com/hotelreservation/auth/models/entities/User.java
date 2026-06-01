package com.hotelreservation.auth.models.entities;

import com.hotelreservation.auth.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a user entity within the system mapped to the "users" database table.
 * <p>
 * This class uses Lombok annotations to automatically generate boilerplate code such as
 * getters, builders, and constructors. It also includes JPA lifecycle callbacks to handle
 * auditing timestamps automatically.
 * </p>
 *
 * <h3>Database Constraints:</h3>
 * <ul>
 * <li>The {@code email} field must be unique (constraint name: {@code uk_user_email}).</li>
 * </ul>
 *
 * @author Gabriel Luque
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}

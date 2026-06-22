package com.hotelreservation.auth.models.entities;

import com.hotelreservation.auth.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a user entity within the system mapped to the "users" database table.
 *
 * <p>This class uses Lombok annotations to automatically generate boilerplate code such as getters,
 * builders, and constructors. It also includes JPA lifecycle callbacks to handle auditing
 * timestamps automatically.
 *
 * <h3>Database Constraints:</h3>
 *
 * <ul>
 *   <li>The {@code email} field must be unique (constraint name: {@code uk_user_email}).
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
    })
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

  @Column(nullable = false)
  private boolean enabled;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "created_at", updatable = false, nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private List<Token> tokens;

  /** Automatically sets timestamps and default status before the entity is persisted. */
  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    this.enabled = true;
  }

  /** Automatically updates the modification timestamp before the entity is updated. */
  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  /**
   * Updates the username of the user.
   *
   * @param username The new unique username to set.
   */
  public void changeUsername(String username) {
    this.username = username;
  }

  /**
   * Updates the email address of the user.
   *
   * @param email The new valid email address to set.
   */
  public void changeEmail(String email) {
    this.email = email;
  }

  /**
   * Updates the user password with a pre-encoded value.
   *
   * @param encodedPassword The new encoded password to set.
   */
  public void changePassword(String encodedPassword) {
    this.password = encodedPassword;
  }
}

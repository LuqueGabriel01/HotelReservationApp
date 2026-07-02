package com.hotelreservation.availability.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** Base class that provides auditing fields for all persistent entities. */
@Getter
@MappedSuperclass
public class AuditableEntity {

  @Column(updatable = false, nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;
}

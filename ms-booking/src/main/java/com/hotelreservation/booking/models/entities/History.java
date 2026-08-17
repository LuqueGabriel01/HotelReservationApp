package com.hotelreservation.booking.models.entities;

import com.hotelreservation.booking.models.enums.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** JPA entity representing an audit history log for booking status transitions. */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "booking_history")
public class History {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking_id", nullable = false)
  private Booking booking;

  @Enumerated(EnumType.STRING)
  private BookingStatus previousStatus;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus newStatus;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  private String reason;

  @PrePersist
  protected void prePersist() {
    this.createdAt = Instant.now();
  }
}

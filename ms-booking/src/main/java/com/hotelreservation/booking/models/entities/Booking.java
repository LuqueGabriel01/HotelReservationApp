package com.hotelreservation.booking.models.entities;

import com.hotelreservation.booking.models.enums.BookingStatus;
import jakarta.persistence.CascadeType;
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
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** JPA entity representing a hotel room booking within the system. */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private UUID userId;

  @Column(nullable = false)
  private UUID hotelId;

  @Column(nullable = false)
  private String hotelName;

  @Column(nullable = false)
  private String hotelAddress;

  @Column(nullable = false)
  private UUID roomId;

  @Column(nullable = false)
  private String roomType;

  @Column(nullable = false)
  private LocalDate checkIn;

  @Column(nullable = false)
  private LocalDate checkOut;

  @Column(nullable = false)
  private Integer nights;

  @Column(nullable = false)
  private BigDecimal pricePerNight;

  @Column(nullable = false)
  private BigDecimal totalPrice;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookingStatus status;

  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "booking",
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private List<History> history;

  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
    if (this.status == null) {
      this.status = BookingStatus.PENDING;
    }
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now();
  }

  /** Updates the booking status to confirmed. */
  public void statusConfirmed() {
    this.status = BookingStatus.CONFIRMED;
  }

  /** Updates the booking status. */
  public void statusCancelled() {
    this.status = BookingStatus.CANCELLED;
  }
}

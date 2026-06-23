package com.hotelreservation.availability.models.entities;

import com.hotelreservation.availability.enums.AvailabilityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/** Entity representing the availability of a specific room. */
@Entity
@Table(
        name = "room_availability",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_room_date",
                        columnNames = {"room_id", "date"}
                )
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailability extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;

    @Column(name = "booking_id")
    private UUID bookingId;
}

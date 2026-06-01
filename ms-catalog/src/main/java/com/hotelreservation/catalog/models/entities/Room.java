package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.enums.RoomType;
import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "rooms",
        indexes = {
                @Index(
                        name = "idx_room_type_capacity",
                        columnList = "type, capacity"
                )
        })
@Getter
@NoArgsConstructor
public class Room extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(nullable = false)
    private BigDecimal pricePerNight;

    @Column(nullable = false)
    private int capacity;

    @Column(length = 150)
    @Size(max = 150)
    private String description;
}

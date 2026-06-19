package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.enums.RoomType;
import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a room available in a hotel.
 */
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

    /** All-arguments domain constructor to initialize a new Room entity instance.
     *
     * @param hotel Parent hotel reference
     * @param type Room category classification
     * @param pricePerNight Cost currency amount per night
     * @param capacity Maximum guest lodging limit
     * @param description Context layout summary information
     */
    public Room(Hotel hotel, RoomType type,
                BigDecimal pricePerNight, int capacity, String description) {
        this.hotel = hotel;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.capacity = capacity;
        this.description = description;
    }

    /**
     * Business logic domain method to safely process partial updates onto fields
     * based on non-null parameters.
     *
     * @param type Optional updated category classification
     * @param pricePerNight Optional updated cost currency amount
     * @param capacity Optional updated guest occupancy count limit
     * @param description Optional updated context narrative summary
     */
    public void update(RoomType type, BigDecimal pricePerNight,
                       Integer capacity, String description){
        if (type != null) this.type = type;
        if (pricePerNight != null) this.pricePerNight = pricePerNight;
        if (capacity != null) this.capacity = capacity;
        if (description != null) this.description = description;
    }
}

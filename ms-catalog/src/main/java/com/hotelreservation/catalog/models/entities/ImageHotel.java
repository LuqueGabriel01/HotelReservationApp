package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

/**
 * Entity representing an image associated with a hotel.
 */
@Entity
@Table(name = "images_hotel")
@Getter
@NoArgsConstructor
public class ImageHotel extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Column(nullable = false)
    @URL
    private String url;

    @Column(nullable = false)
    private boolean isMain = false;
}

package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a hotel in the system.
 */
@Entity
@Table(
        name = "hotels",
        indexes = {
                @Index(
                        name = "idx_hotel_city_stars",
                        columnList = "city, stars"
                )
        })
@Getter
@NoArgsConstructor
public class Hotel extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 150)
    @Size(max = 150)
    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    @Min(1)
    @Max(5)
    private int stars;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ImageHotel> imagesHotel = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "hotel_amenities",
            joinColumns = @JoinColumn(name = "hotel_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities = new ArrayList<>();

    public void update(String name, String description, String address,
                       String city, Integer stars, List<Amenity> amenities) {

        if (name != null)        this.name = name;
        if (description != null) this.description = description;
        if (address != null)     this.address = address;
        if (city != null)        this.city = city;
        if (stars != null)       this.stars = stars;
        if (amenities != null){
          amenities.stream()
                  .filter(amenity -> !this.amenities.contains(amenity))
                  .forEach(this.amenities::add);
        }

    }
}

package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "amenities")
@Getter
@NoArgsConstructor
public class Amenity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    @Size(max = 50)
    private String name;

    @ManyToMany(mappedBy = "amenities")
    private List<Hotel> hotels = new ArrayList<>();
}

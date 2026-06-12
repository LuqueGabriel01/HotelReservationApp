package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
/**
 * JPA repository for {@link Amenity} persistence operations
 */
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
}

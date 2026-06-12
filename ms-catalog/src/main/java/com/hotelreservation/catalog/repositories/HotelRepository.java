package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for {@link Hotel} persistence operations
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {

    /**
     * Finds a hotel by their name.
     * @param name The name to search. Cannot be {@code null}.
     * @return {@link Optional} containing the hotel if found, empty otherwise.
     */
    Optional<Hotel> findByName(String name);
}

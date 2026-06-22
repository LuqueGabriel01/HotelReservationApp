package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Hotel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** JPA repository for {@link Hotel} persistence operations. */
@Repository
public interface HotelRepository
    extends JpaRepository<Hotel, UUID>, JpaSpecificationExecutor<Hotel> {

  /**
   * Checks if a hotel by name and city.
   *
   * @param name The name of the hotel and the city. Cannot be {@code null}.
   * @return true if found, false otherwise.
   */
  boolean existsByNameAndCity(String name, String city);
}

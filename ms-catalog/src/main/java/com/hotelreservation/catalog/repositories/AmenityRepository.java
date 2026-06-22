package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Amenity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** JPA repository for {@link Amenity} persistence operations. */
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, UUID> {

  /**
   * Retrieves a collection of amenities whose names match any of the values in the provided list.
   *
   * @param names A list of amenity name strings to look up
   * @return A list of matching Amenity entities found in the database
   */
  List<Amenity> findByNameIn(List<String> names);
}

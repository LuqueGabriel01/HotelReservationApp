package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.ImageHotel;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** JPA repository for {@link ImageHotel} persistence operations. */
public interface ImageHotelRepository extends JpaRepository<ImageHotel, UUID> {

  /**
   * Retrieves the image designated as the primary showcase graphic for a specific hotel.
   *
   * @param hotelId The unique identifier of the target hotel.
   * @return An Optional containing the matching main ImageHotel entity, or empty if none is found.
   */
  @Query("SELECT i FROM ImageHotel i WHERE i.hotel.id = :hotelId AND i.isMain = true")
  Optional<ImageHotel> findMainImageByHotelId(@Param("hotelId") UUID hotelId);
}

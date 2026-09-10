package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Room;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** JPA repository for {@link Room} persistence operations. */
public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

  /**
   * Returns the cheapest room price per hotel for the given hotel ids. Hotels with no rooms are
   * simply absent from the result.
   *
   * @param hotelIds Hotel identifiers to aggregate over
   * @return One projection per hotel that has at least one room
   */
  @Query(
      "SELECT r.hotel.id AS hotelId, MIN(r.pricePerNight) AS minPrice "
          + "FROM Room r WHERE r.hotel.id IN :hotelIds GROUP BY r.hotel.id")
  List<HotelMinPriceProjection> findMinPricePerNightByHotelIds(
      @Param("hotelIds") Collection<UUID> hotelIds);
}

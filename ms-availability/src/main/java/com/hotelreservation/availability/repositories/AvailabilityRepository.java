package com.hotelreservation.availability.repositories;
import com.hotelreservation.availability.models.entities.RoomAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** JPA repository for {@link RoomAvailability} persistence operations. */
@Repository
public interface AvailabilityRepository extends JpaRepository<RoomAvailability, UUID> {

    /**
     * Finds IDs of rooms that are occupied or blocked within specific dates.
     *
     * @param roomIds List of room IDs to check.
     * @param dates   List of dates to check.
     * @return Set of occupied room IDs.
     */
    @Query(
            """
            SELECT DISTINCT a.roomId FROM RoomAvailability a
            WHERE a.roomId IN :roomIds
            AND a.date IN :dates
            AND a.status IN ('RESERVED', 'BLOCKED')
            """)
    Set<UUID> findOccupiedRoomIds(
            @Param("roomIds")List<UUID> roomIds,
            @Param("dates") List<LocalDate> dates
            );


    /**
     * Finds availability records for a room on specific dates.
     *
     * @param roomId Target room ID.
     * @param dates  List of dates to query.
     * @return List of availability records.
     */
    List<RoomAvailability> findByRoomIdAndDateIn(
            UUID roomId,
            List<LocalDate> dates
    );

    /**
     * Counts availability records for a room on specific dates.
     *
     * @param roomId Target room ID.
     * @param dates  List of dates to check.
     * @return Number of occupied dates.
     */
    int countByRoomIdAndDateIn(UUID roomId, List<LocalDate> dates);

    void deleteByRoomIdAndDateIn(UUID roomId, List<LocalDate> dates);

}

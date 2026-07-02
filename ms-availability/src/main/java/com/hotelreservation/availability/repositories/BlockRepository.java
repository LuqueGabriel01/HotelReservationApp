package com.hotelreservation.availability.repositories;

import com.hotelreservation.availability.models.entities.RoomBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** JPA repository for {@link RoomBlock} persistence operations. */
@Repository
public interface BlockRepository extends JpaRepository<RoomBlock, UUID> {

    /**
     * Retrieves all room blocks that have expired before the specified timestamp.
     *
     * @param now the current timestamp threshold to check against
     * @return a {@link List} of expired {@link RoomBlock} entities
     */
    List<RoomBlock> findByExpiresAtBefore(Timestamp now);
}

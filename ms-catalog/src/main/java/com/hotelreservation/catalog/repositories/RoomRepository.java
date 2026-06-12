package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
/**
 * JPA repository for {@link Room} persistence operations
 */
public interface RoomRepository extends JpaRepository<Room, UUID> {
}

package com.hotelreservation.availability.repositories;

import com.hotelreservation.availability.models.entities.RoomBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/** JPA repository for {@link RoomBlock} persistence operations. */
@Repository
public interface BlockRepository extends JpaRepository<RoomBlock, UUID> {


}

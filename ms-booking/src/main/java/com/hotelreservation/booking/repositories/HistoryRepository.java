package com.hotelreservation.booking.repositories;

import com.hotelreservation.booking.models.entities.History;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository providing database access operations for {@link History} entities. */
public interface HistoryRepository extends JpaRepository<History, UUID> {}

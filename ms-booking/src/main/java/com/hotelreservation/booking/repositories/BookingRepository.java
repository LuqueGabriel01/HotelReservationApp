package com.hotelreservation.booking.repositories;

import com.hotelreservation.booking.models.entities.Booking;
import com.hotelreservation.booking.models.enums.BookingStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** Spring Data JPA repository providing database access operations for {@link Booking} entities. */
public interface BookingRepository extends JpaRepository<Booking, UUID> {

  List<Booking> findByCheckInIsAndStatus(LocalDate checkIn, BookingStatus status);

  Page<Booking> findAllByHotelId(UUID hotelId, Pageable pageable);

  @Query(
      """
      SELECT COUNT(b) > 0 FROM Booking b
      WHERE b.roomId = :roomId
      AND b.status = 'CONFIRMED'
      AND b.checkIn < :checkOut
      AND b.checkOut > :checkIn
      """)
  boolean existsOverlappingBooking(UUID roomId, LocalDate checkIn, LocalDate checkOut);

  @Query(
      """
      SELECT b FROM Booking b
      WHERE b.userId = :userId
      AND b.status != 'PENDING'
      AND (:status IS NULL OR b.status = :status)
      """)
  Page<Booking> findByUserIdAndStatus(UUID userId, BookingStatus status, Pageable pageable);
}

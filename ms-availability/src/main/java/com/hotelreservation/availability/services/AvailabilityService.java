package com.hotelreservation.availability.services;

import com.hotelreservation.availability.client.CatalogClient;
import com.hotelreservation.availability.constants.ErrorConstants;
import com.hotelreservation.availability.enums.AvailabilityStatus;
import com.hotelreservation.availability.exception.ExternalServiceUnavailableException;
import com.hotelreservation.availability.exception.InvalidDateRangeException;
import com.hotelreservation.availability.exception.NotFoundException;
import com.hotelreservation.availability.mapper.RoomAvailabilityMapper;
import com.hotelreservation.availability.models.dtos.external.RoomDto;
import com.hotelreservation.availability.models.dtos.request.ReleaseRoomRequestDto;
import com.hotelreservation.availability.models.dtos.request.ReserveRoomRequestDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityResponseDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityRoomResponseDto;
import com.hotelreservation.availability.models.dtos.response.roomblock.BlockReleaseResponseDto;
import com.hotelreservation.availability.models.dtos.response.roomblock.ReserveRoomResponseDto;
import com.hotelreservation.availability.models.entities.RoomAvailability;
import com.hotelreservation.availability.models.entities.RoomBlock;
import com.hotelreservation.availability.repositories.AvailabilityRepository;
import com.hotelreservation.availability.repositories.BlockRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/** Service for managing hotel room availability logic. */
@Service
@RequiredArgsConstructor
public class AvailabilityService {

  private final AvailabilityRepository availabilityRepository;
  private final CatalogClient catalogClient;
  private final RoomAvailabilityMapper mapper;
  private final BlockRepository blockRepository;

  /**
   * Retrieves availability status for all rooms in a hotel within a date range.
   *
   * @param hotelId The unique ID of the hotel.
   * @param checkIn The start date of the range.
   * @param checkOut The end date of the range.
   * @return List of rooms with their availability status.
   * @throws InvalidDateRangeException If the date range is invalid.
   * @throws NotFoundException If the hotel is not found.
   * @throws ExternalServiceUnavailableException If the catalog service fails.
   */
  public List<AvailabilityResponseDto> getAllAvailableRooms(
      UUID hotelId, LocalDate checkIn, LocalDate checkOut) {

    if (checkIn.equals(checkOut) || checkOut.isBefore(checkIn)) {
      throw new InvalidDateRangeException(ErrorConstants.Error.DATE_ERROR);
    }

    List<LocalDate> dates = checkIn.datesUntil(checkOut).toList();

    List<RoomDto> rooms;

    try {
      rooms = catalogClient.getRoomsByHotel(hotelId);
    } catch (WebClientResponseException.NotFound e) {
      throw new NotFoundException(
          ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
              + hotelId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    } catch (WebClientRequestException | WebClientResponseException e) {
      throw new ExternalServiceUnavailableException(ErrorConstants.Error.MS_CATALOG_UNAVAILABLE);
    }

    List<UUID> roomIds = rooms.stream().map(RoomDto::id).toList();

    Set<UUID> occupiedRoomIds = availabilityRepository.findOccupiedRoomIds(roomIds, dates);

    return rooms.stream()
        .map(room -> mapper.toAvailabilityResponseDto(room, !occupiedRoomIds.contains(room.id())))
        .toList();
  }

  /**
   * Retrieves detailed availability and blockage reasons for a specific room.
   *
   * @param hotelId The unique ID of the hotel.
   * @param roomId The unique ID of the room.
   * @param checkIn The start date of the range.
   * @param checkOut The end date of the range.
   * @return Detailed availability response for the target room.
   * @throws InvalidDateRangeException If the date range is invalid.
   * @throws NotFoundException If the room is not found.
   * @throws ExternalServiceUnavailableException If the catalog service fails.
   */
  public AvailabilityRoomResponseDto getRoomAvailabilityById(
      UUID hotelId, UUID roomId, LocalDate checkIn, LocalDate checkOut) {

    if (checkIn.equals(checkOut) || checkOut.isBefore(checkIn)) {
      throw new InvalidDateRangeException(ErrorConstants.Error.DATE_ERROR);
    }

    List<LocalDate> dates = checkIn.datesUntil(checkOut).toList();

    RoomDto room;

    try {
      room = catalogClient.getRoomById(hotelId, roomId);
    } catch (WebClientResponseException.NotFound e) {
      throw new NotFoundException(
          ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
              + roomId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    } catch (WebClientRequestException e) {
      throw new ExternalServiceUnavailableException(ErrorConstants.Error.MS_CATALOG_UNAVAILABLE);
    }

    List<RoomAvailability> roomInGivenDates =
        availabilityRepository.findByRoomIdAndDateIn(roomId, dates);

    boolean available = roomInGivenDates.isEmpty();

    AvailabilityStatus reason =
        available
            ? null
            : roomInGivenDates.stream()
                .map(RoomAvailability::getStatus)
                .filter(status -> status == AvailabilityStatus.RESERVED)
                .findFirst()
                .orElse(AvailabilityStatus.BLOCKED);

    return new AvailabilityRoomResponseDto(roomId, checkIn, checkOut, available, reason);
  }

  /**
   * Converts an existing room block into a confirmed room reservation.
   *
   * <p>This method removes the temporary block status for the given dates, persists the new
   * reservation status linked to the booking, and deletes the block record.
   *
   * @param reserveRoomRequestDto the request data containing the block ID and booking ID
   * @param roomId the unique identifier of the room being reserved
   * @return a {@link ReserveRoomResponseDto} confirming the reservation details
   * @throws NotFoundException if the temporary room block does not exist
   */
  @Transactional
  public ReserveRoomResponseDto reserveBooking(
      ReserveRoomRequestDto reserveRoomRequestDto, UUID roomId) {

    RoomBlock roomBlock =
        blockRepository
            .findById(reserveRoomRequestDto.lockId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.ROOM_BLOCK_WITH_ID
                            + reserveRoomRequestDto.lockId()
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    List<LocalDate> dates = roomBlock.getCheckIn().datesUntil(roomBlock.getCheckOut()).toList();

    availabilityRepository.deleteByRoomIdAndDateIn(roomBlock.getRoomId(), dates);

    List<RoomAvailability> reservedDates =
        dates.stream()
            .map(
                date ->
                    RoomAvailability.builder()
                        .roomId(roomId)
                        .date(date)
                        .status(AvailabilityStatus.RESERVED)
                        .bookingId(reserveRoomRequestDto.bookingId())
                        .build())
            .toList();

    availabilityRepository.saveAll(reservedDates);
    blockRepository.delete(roomBlock);

    return new ReserveRoomResponseDto(
        roomId, reserveRoomRequestDto.bookingId(), AvailabilityStatus.RESERVED);
  }

  /**
   * Releases a room reservation by removing its availability records based on the booking ID.
   *
   * @param requestDto the request data containing the booking ID to release
   * @param roomId the unique identifier of the room being released
   * @return a {@link BlockReleaseResponseDto} confirming the release status
   * @throws NotFoundException if no active reservation is found for the given booking ID
   */
  @Transactional
  public BlockReleaseResponseDto releaseBooking(ReleaseRoomRequestDto requestDto, UUID roomId) {

    List<RoomAvailability> bookedRooms =
        availabilityRepository.findByBookingId(requestDto.bookingId()).stream().toList();

    if (bookedRooms.isEmpty()) {
      throw new NotFoundException(
          ErrorConstants.Error.BOOKING_WITH_ID
              + requestDto.bookingId()
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    }

    availabilityRepository.deleteAll(bookedRooms);

    return new BlockReleaseResponseDto(true, roomId, requestDto.bookingId());
  }
}

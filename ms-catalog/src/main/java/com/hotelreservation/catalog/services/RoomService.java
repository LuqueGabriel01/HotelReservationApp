package com.hotelreservation.catalog.services;

import com.hotelreservation.catalog.constants.ErrorConstants;
import com.hotelreservation.catalog.exceptions.InvalidFilterException;
import com.hotelreservation.catalog.exceptions.NotFoundException;
import com.hotelreservation.catalog.mappers.RoomMapper;
import com.hotelreservation.catalog.models.dto.request.room.CreateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.request.room.RoomFilterRequest;
import com.hotelreservation.catalog.models.dto.request.room.UpdateRoomRequestDto;
import com.hotelreservation.catalog.models.dto.response.room.RoomResponseDto;
import com.hotelreservation.catalog.models.entities.Hotel;
import com.hotelreservation.catalog.models.entities.Room;
import com.hotelreservation.catalog.repositories.HotelRepository;
import com.hotelreservation.catalog.repositories.RoomRepository;
import com.hotelreservation.catalog.specifications.RoomSpecification;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/** Service responsible for room business logic operations. */
@Service
@RequiredArgsConstructor
public class RoomService {

  private final HotelRepository hotelRepository;
  private final RoomRepository roomRepository;
  private final RoomMapper roomMapper;

  /**
   * Retrieves a filtered list of rooms belonging to a specific hotel. Validates that the hotel
   * exists and ensures all filter parameters fall within acceptable bounds.
   *
   * @param filter The specifications criteria used to filter rooms (type, capacity, price ranges).
   *     Cannot be null.
   * @param hotelId The unique identifier of the target parent hotel.
   * @return A list of mapped room response DTOs matching the criteria.
   * @throws NotFoundException If no hotel matches the provided hotel identifier.
   * @throws InvalidFilterException If the filter is null, contains negative constraints, or if min
   *     price exceeds max price.
   */
  public List<RoomResponseDto> findAllRooms(RoomFilterRequest filter, UUID hotelId) {

    if (!hotelRepository.existsById(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
              + hotelId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    }

    validateFilter(filter);

    Specification<Room> spec =
        Specification.where(RoomSpecification.hasCapacity(filter.capacity()))
            .and(RoomSpecification.hasType(filter.type()))
            .and(RoomSpecification.hasPriceBetween(filter.minPrice(), filter.maxPrice()));

    List<Room> rooms = roomRepository.findAll(spec);

    return rooms.stream().map(roomMapper::toResponseDto).toList();
  }

  /**
   * Helper internal method to safely validate room query filter configurations. Separated to reduce
   * method cyclomatic complexity under acceptable linter metrics.
   */
  private void validateFilter(RoomFilterRequest filter) {
    if (filter == null) {
      throw new InvalidFilterException("Filter request can not be null");
    }

    validateDimensions(filter);
    validatePriceBounds(filter);
  }

  /** Validates capacity boundaries for the provided filter. */
  private void validateDimensions(RoomFilterRequest filter) {
    if (filter.capacity() != null && filter.capacity() < 0) {
      throw new InvalidFilterException("Capacity can not be a negative number");
    }
  }

  /** Validates pricing logic constraints and consistency. */
  private void validatePriceBounds(RoomFilterRequest filter) {
    if (filter.minPrice() != null && filter.minPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidFilterException("Min price cannot be negative");
    }

    if (filter.maxPrice() != null && filter.maxPrice().compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidFilterException("Max price cannot be negative");
    }

    if (filter.minPrice() != null && filter.maxPrice() != null) {
      checkPriceRange(filter.minPrice(), filter.maxPrice());
    }
  }

  /** Verifies that the minimum price threshold does not exceed the maximum pricing cap. */
  private void checkPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
    if (minPrice.compareTo(maxPrice) > 0) {
      throw new InvalidFilterException("Min price cannot be greater than max price");
    }
  }

  /**
   * Retrieves the profile details of a specific room, verifying its relationship with the parent
   * hotel.
   *
   * @param hotelId The unique identifier of the parent hotel.
   * @param id The unique identifier of the target room.
   * @return The mapped room details data transfer object.
   * @throws NotFoundException If the hotel doesn't exist, the room doesn't exist, or the room
   *     doesn't belong to the hotel.
   */
  public RoomResponseDto findRoomById(UUID hotelId, UUID id) {

    if (!hotelRepository.existsById(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
              + hotelId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    }

    Room room =
        roomRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
                            + id
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    if (!room.getHotel().getId().equals(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
              + id
              + ErrorConstants.Error.NOT_FOUND_SUFFIX_IN_
              + " hotel with id "
              + hotelId);
    }

    return roomMapper.toResponseDto(room);
  }

  /**
   * Creates and persists a new room associated with a specific hotel.
   *
   * @param newRoom The specification properties payload for the new room. Cannot be null.
   * @param hotelId The unique identifier of the parent hotel hosting the room.
   * @return The data transfer object of the newly persisted room.
   * @throws IllegalArgumentException If the new room request data payload is null.
   * @throws NotFoundException If the parent hotel cannot be found.
   */
  @Transactional
  public RoomResponseDto createRoom(CreateRoomRequestDto newRoom, UUID hotelId) {

    if (newRoom == null) {
      throw new IllegalArgumentException("Room data cannot be null");
    }

    Hotel hotel =
        hotelRepository
            .findById(hotelId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
                            + hotelId
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    Room room =
        new Room(
            hotel,
            newRoom.type(),
            newRoom.pricePerNight(),
            newRoom.capacity(),
            newRoom.description());

    Room savedRoom = roomRepository.save(room);

    return roomMapper.toResponseDto(savedRoom);
  }

  /**
   * Updates structural field data on an existing room record after validating context ownership.
   *
   * @param updatedRoom The container containing patch properties fields to apply. Cannot be null.
   * @param hotelId The unique identifier of the parent hotel.
   * @param id The unique identifier of the target room to modify.
   * @return The updated and mapped room response structure.
   * @throws IllegalArgumentException If the update request payload object is null.
   * @throws NotFoundException If the parent hotel, the target room, or the relationship ownership
   *     map fails validation.
   */
  @Transactional
  public RoomResponseDto updateRoom(UpdateRoomRequestDto updatedRoom, UUID hotelId, UUID id) {
    if (updatedRoom == null) {
      throw new IllegalArgumentException("Room data cannot be null");
    }

    if (!hotelRepository.existsById(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX
              + hotelId
              + ErrorConstants.Error.NOT_FOUND_SUFFIX);
    }

    Room room =
        roomRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
                            + id
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    if (!room.getHotel().getId().equals(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
              + id
              + ErrorConstants.Error.NOT_FOUND_SUFFIX_IN_
              + " hotel with id "
              + hotelId);
    }

    room.update(
        updatedRoom.type(),
        updatedRoom.pricePerNight(),
        updatedRoom.capacity(),
        updatedRoom.description());

    Room savedRoom = roomRepository.save(room);
    return roomMapper.toResponseDto(savedRoom);
  }

  /**
   * Removes an existing room entity from the database ecosystem after conducting security relation
   * checks.
   *
   * @param hotelId The unique identifier of the parent hotel.
   * @param id The unique identifier of the target room scheduled for deletion.
   * @throws NotFoundException If the target room does not exist, or does not belong to the
   *     specified hotel.
   */
  @Transactional
  public void deleteRoom(UUID hotelId, UUID id) {

    Room room =
        roomRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
                            + id
                            + ErrorConstants.Error.NOT_FOUND_SUFFIX));

    if (!room.getHotel().getId().equals(hotelId)) {
      throw new NotFoundException(
          ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX
              + id
              + ErrorConstants.Error.NOT_FOUND_SUFFIX_IN_
              + " hotel with id "
              + hotelId);
    }

    roomRepository.delete(room);
  }
}

package com.hotelreservation.availability.services;

import com.hotelreservation.availability.client.CatalogClient;
import com.hotelreservation.availability.constants.ErrorConstants;
import com.hotelreservation.availability.enums.AvailabilityStatus;
import com.hotelreservation.availability.exception.ExternalServiceUnavailableException;
import com.hotelreservation.availability.exception.InvalidDateRangeException;
import com.hotelreservation.availability.exception.NotFoundException;
import com.hotelreservation.availability.mapper.RoomAvailabilityMapper;
import com.hotelreservation.availability.models.dtos.external.RoomDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityResponseDto;
import com.hotelreservation.availability.models.dtos.response.availability.AvailabilityRoomResponseDto;
import com.hotelreservation.availability.models.entities.RoomAvailability;
import com.hotelreservation.availability.repositories.AvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Service for managing hotel room availability logic.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final CatalogClient catalogClient;
    private final RoomAvailabilityMapper mapper;

    /**
     * Retrieves availability status for all rooms in a hotel within a date range.
     *
     * @param hotelId  The unique ID of the hotel.
     * @param checkIn  The start date of the range.
     * @param checkOut The end date of the range.
     * @return List of rooms with their availability status.
     * @throws InvalidDateRangeException          If the date range is invalid.
     * @throws NotFoundException                  If the hotel is not found.
     * @throws ExternalServiceUnavailableException If the catalog service fails.
     */
    public List<AvailabilityResponseDto> getAllAvailableRooms(UUID hotelId, LocalDate checkIn, LocalDate checkOut){

        if (checkIn.equals(checkOut) || checkOut.isBefore(checkIn)){
            throw new InvalidDateRangeException(ErrorConstants.Error.DATE_ERROR);
        }

        List<LocalDate> dates = checkIn.datesUntil(checkOut).toList();

        List<RoomDto> rooms;

        try{
            rooms = catalogClient.getRoomsByHotel(hotelId);
        }catch (WebClientResponseException.NotFound e){
            throw new NotFoundException(ErrorConstants.Error.HOTEL_NOT_FOUND_PREFIX + hotelId + ErrorConstants.Error.NOT_FOUND_SUFFIX);
        }catch (WebClientRequestException | WebClientResponseException e){
            throw new ExternalServiceUnavailableException(ErrorConstants.Error.MS_CATALOG_UNAVAILABLE);
        }

        List<UUID> roomIds = rooms.stream()
                .map(RoomDto::id)
                .toList();

        Set<UUID> occupiedRoomIds = availabilityRepository.findOccupiedRoomIds(roomIds, dates);

        return rooms.stream()
                .map(room -> mapper.toAvailabilityResponseDto(
                        room,
                        !occupiedRoomIds.contains(room.id())
                ))
                .toList();
    }

    /**
     * Retrieves detailed availability and blockage reasons for a specific room.
     *
     * @param hotelId  The unique ID of the hotel.
     * @param roomId   The unique ID of the room.
     * @param checkIn  The start date of the range.
     * @param checkOut The end date of the range.
     * @return Detailed availability response for the target room.
     * @throws InvalidDateRangeException          If the date range is invalid.
     * @throws NotFoundException                  If the room is not found.
     * @throws ExternalServiceUnavailableException If the catalog service fails.
     */
    public AvailabilityRoomResponseDto getRoomAvailabilityById(UUID hotelId, UUID roomId, LocalDate checkIn, LocalDate checkOut){

        if (checkIn.equals(checkOut) || checkOut.isBefore(checkIn)){
            throw new InvalidDateRangeException(ErrorConstants.Error.DATE_ERROR);
        }

        List<LocalDate> dates = checkIn.datesUntil(checkOut).toList();

        RoomDto room;

        try{
            room = catalogClient.getRoomById(hotelId, roomId);
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException(ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX + roomId + ErrorConstants.Error.NOT_FOUND_SUFFIX);
        }catch (WebClientRequestException e){
            throw new ExternalServiceUnavailableException(ErrorConstants.Error.MS_CATALOG_UNAVAILABLE);
        }

        List<RoomAvailability> roomInGivenDates = availabilityRepository.findByRoomIdAndDateIn(roomId, dates);

        boolean available = roomInGivenDates.isEmpty();

        AvailabilityStatus reason = available ? null : roomInGivenDates.stream()
                .map(RoomAvailability::getStatus)
                .filter(status -> status == AvailabilityStatus.RESERVED)
                .findFirst()
                .orElse(AvailabilityStatus.BLOCKED);

        return new AvailabilityRoomResponseDto(
                roomId,
                checkIn,
                checkOut,
                available,
                reason);
    }
}

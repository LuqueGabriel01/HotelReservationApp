package com.hotelreservation.availability.services;

import com.hotelreservation.availability.client.CatalogClient;
import com.hotelreservation.availability.constants.ErrorConstants;
import com.hotelreservation.availability.enums.AvailabilityStatus;
import com.hotelreservation.availability.exception.*;
import com.hotelreservation.availability.mapper.RoomBlockMapper;
import com.hotelreservation.availability.models.dtos.external.RoomDto;
import com.hotelreservation.availability.models.dtos.response.roomblock.RoomBlockResponseDto;
import com.hotelreservation.availability.models.entities.RoomAvailability;
import com.hotelreservation.availability.models.entities.RoomBlock;
import com.hotelreservation.availability.repositories.AvailabilityRepository;
import com.hotelreservation.availability.repositories.BlockRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing room block logic.
 */
@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final RoomBlockMapper mapper;
    private final CatalogClient catalogClient;
    private final AvailabilityRepository availabilityRepository;

    /**
     * Blocks a specific hotel room for a user within a given date range.
     * * @param hotelId   the unique identifier of the hotel
     * @param roomId    the unique identifier of the room to block
     * @param userId    the unique identifier of the user making the block
     * @param checkIn   the start date of the block period
     * @param checkOut  the end date of the block period
     * @return a {@link RoomBlockResponseDto} containing the details of the created block
     * @throws InvalidDateRangeException          if the date range is invalid (same day or checkout before check-in)
     * @throws NotFoundException                  if the requested room does not exist in the catalog
     * @throws ExternalServiceUnavailableException if the catalog service is unreachable
     * @throws RoomNotAvailableException          if the room is already occupied or blocked for the requested dates
     */
    @Transactional
    public RoomBlockResponseDto blockRoom(UUID hotelId, UUID roomId, UUID userId, LocalDate checkIn, LocalDate checkOut){

        if (checkIn.equals(checkOut) || checkOut.isBefore(checkIn)){
            throw new InvalidDateRangeException(ErrorConstants.Error.DATE_ERROR);
        }

        List<LocalDate> dates = checkIn.datesUntil(checkOut).toList();

        try{
            RoomDto room = catalogClient.getRoomById(hotelId, roomId);
        } catch (WebClientResponseException.NotFound e) {
            throw new NotFoundException(ErrorConstants.Error.ROOM_NOT_FOUND_PREFIX + roomId + ErrorConstants.Error.NOT_FOUND_SUFFIX);
        }catch (WebClientRequestException e){
            throw new ExternalServiceUnavailableException(ErrorConstants.Error.MS_CATALOG_UNAVAILABLE);
        }

        boolean isOccupied = availabilityRepository.countByRoomIdAndDateIn(roomId, dates) > 0;

        if (isOccupied){
            throw new RoomNotAvailableException(ErrorConstants.Error.ROOM_UNAVAILABLE);
        }

        RoomBlock roomBlock = RoomBlock.builder()
                .roomId(roomId)
                .userId(userId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .expiresAt(Timestamp.from(Instant.now().plus(10, ChronoUnit.MINUTES)))
                .build();

        RoomBlock saved = blockRepository.save(roomBlock);

        List<RoomAvailability> blockedDates = checkIn.datesUntil(checkOut)
                .map(date -> RoomAvailability.builder()
                        .roomId(roomId)
                        .date(date)
                        .status(AvailabilityStatus.BLOCKED)
                        .build())
                .toList();

        availabilityRepository.saveAll(blockedDates);

        return mapper.toRoomBlockResponseDto(saved);
    }

    /**
     * Deletes a specific room block if it belongs to the requesting user.
     * <p>
     * This operation also releases the associated blocked dates from the availability record.
     *
     * @param userId the unique identifier of the user requesting the deletion
     * @param lockId the unique identifier of the room block to delete
     * @throws NotFoundException  if no room block is found with the given ID
     * @throws ForbiddenException if the room block does not belong to the requesting user
     */
    @Transactional
    public void deleteRoomBlock(UUID userId, UUID lockId){

        RoomBlock roomBlock = blockRepository.findById(lockId)
                .orElseThrow(() -> new NotFoundException(ErrorConstants.Error.ROOM_BLOCK_WITH_ID + lockId + ErrorConstants.Error.NOT_FOUND_SUFFIX));

        if (!roomBlock.getUserId().equals(userId)){
            throw new ForbiddenException(ErrorConstants.Error.ROOM_BLOCKED_NOT_OWNED);
        }

        List<LocalDate> dates = roomBlock.getCheckIn().datesUntil(roomBlock.getCheckOut()).toList();
        availabilityRepository.deleteByRoomIdAndDateIn(roomBlock.getRoomId(), dates);
        blockRepository.delete(roomBlock);
    }

}

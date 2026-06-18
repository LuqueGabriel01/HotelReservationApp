package com.hotelreservation.catalog.services;

import com.hotelreservation.catalog.exceptions.InvalidFilterException;
import com.hotelreservation.catalog.exceptions.InvalidPaginationException;
import com.hotelreservation.catalog.exceptions.NotFoundException;
import com.hotelreservation.catalog.mappers.HotelMapper;
import com.hotelreservation.catalog.models.dto.request.hotel.CreateHotelRequestDto;
import com.hotelreservation.catalog.models.dto.request.hotel.HotelFilterRequest;
import com.hotelreservation.catalog.models.dto.request.hotel.UpdateHotelRequestDto;
import com.hotelreservation.catalog.models.dto.response.PageResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.CreateHotelResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelDetailResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.HotelSummaryResponseDto;
import com.hotelreservation.catalog.models.dto.response.hotel.UpdateHotelResponseDto;
import com.hotelreservation.catalog.models.entities.Amenity;
import com.hotelreservation.catalog.models.entities.Hotel;
import com.hotelreservation.catalog.repositories.AmenityRepository;
import com.hotelreservation.catalog.repositories.HotelRepository;
import com.hotelreservation.catalog.specifications.HotelSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for hotel business logic operations.
 */
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    /**
     * Returns a paginated and filtered list of hotels.
     *
     * @param filter Filter the hotels by city name and stars
     * @param page Page number
     * @param size Page size
     * @return
     */
    public PageResponseDto<HotelSummaryResponseDto> findAllHotels(HotelFilterRequest filter, int page, int size){

        if (page <0 ){
            throw new InvalidPaginationException("Page number can not be negative");
        }

        if (size <= 0 || size > 100){
            throw new InvalidPaginationException("Page size must be between 1 and 100");
        }

        if (filter.city() != null && filter.city().isBlank()){
           throw new InvalidFilterException("City name cannot be empty");
        }
        if (filter.stars() != null && (filter.stars() < 1 || filter.stars() > 5)){
            throw new InvalidFilterException("Stars must be between 1 and 5");
        }

        PageRequest pageable = PageRequest.of(page, size);

        Specification<Hotel> spec = Specification.
                where(HotelSpecification.hasCity(filter.city()))
                .and(HotelSpecification.hasStars(filter.stars()))
                .and(HotelSpecification.hasAmenities(filter.amenityNames()));

        Page<Hotel> hotels = hotelRepository.findAll(spec, pageable);

        List<HotelSummaryResponseDto> content = hotels.getContent()
                .stream()
                .map(hotelMapper::toSummaryDto)
                .toList();

        return new PageResponseDto<>(
                content,
                hotels.getNumber(),
                hotels.getSize(),
                hotels.getTotalElements(),
                hotels.getTotalPages()
        );
    }

    public HotelDetailResponseDto findHotelById(UUID id){

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel with id " + id + " not found"));

        return hotelMapper.toDetailDto(hotel);

    }

    @Transactional
    public CreateHotelResponseDto createHotel(CreateHotelRequestDto newHotel){

        if (newHotel == null){
            throw new IllegalArgumentException("Hotel data cannot be null");
        }
        if (hotelRepository.existsByNameAndCity(newHotel.name(), newHotel.city())){
           throw new ResponseStatusException(HttpStatus.CONFLICT, "Hotel already exists.");
        }

        List<Amenity> amenities = amenityRepository.findByNameIn(newHotel.amenities());

        Hotel hotel = new Hotel(
                newHotel.name(),
                newHotel.description(),
                newHotel.address(),
                newHotel.city(),
                newHotel.stars(),
                amenities
        );

        Hotel savedHotel = hotelRepository.save(hotel);

        return hotelMapper.toCreateResponseDto(savedHotel);

    }

    @Transactional
    public UpdateHotelResponseDto updateHotel(UpdateHotelRequestDto updatedHotel, UUID id){

        if (updatedHotel == null){
            throw new IllegalArgumentException("New hotel data cannot be null");
        }

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel with id " + id + " not found"));

        List<Amenity> amenities = null;
        if (updatedHotel.amenities() != null && !updatedHotel.amenities().isEmpty()){
            amenities = amenityRepository.findByNameIn(updatedHotel.amenities());
            if (amenities.size() != updatedHotel.amenities().size()){
                throw new InvalidFilterException("One or more amenities do not exist");
            }
        }

        hotel.update(
                updatedHotel.name(),
                updatedHotel.description(),
                updatedHotel.address(),
                updatedHotel.city(),
                updatedHotel.stars(),
                amenities
        );

        Hotel savedHotel = hotelRepository.save(hotel);
        return  hotelMapper.toUpdateResponseDto(savedHotel);
    }

    @Transactional
    public void deleteHotel(UUID id){
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel with id " + id + " not found"));
        //Check for images in Cloudinary if existing.
        hotelRepository.delete(hotel);
    }

}

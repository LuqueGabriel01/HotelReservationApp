package com.hotelreservation.catalog.specifications;

import com.hotelreservation.catalog.enums.RoomType;
import com.hotelreservation.catalog.exceptions.InvalidFilterException;
import com.hotelreservation.catalog.models.entities.Room;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Factory class providing reusable JPA Criteria API specifications for filtering Room entities.
 */
public class RoomSpecification {

    /**
     * Creates a specification to filter rooms by their category classification type.
     * Maps the incoming string to the {@link RoomType} enum.
     *
     * @param type The target room type string (e.g., "SUITE"). If null, the filter is ignored.
     * @return A Specification for the query matching the room type enum.
     * @throws InvalidFilterException If the provided type string does not match any valid enum literal.
     */
    public static Specification<Room>  hasType(String type){
        return ((root, query, criteriaBuilder) -> {
            if (type == null) return null;
            try{
                RoomType roomType = RoomType.valueOf(type.toUpperCase());
                return criteriaBuilder.equal(root.get("type"), roomType);
            } catch (IllegalArgumentException e){
                throw new InvalidFilterException("Invalid room type: " + type);
            }
        });
    }

    /**
     * Creates a specification to filter rooms that can accommodate at least a minimum number of guests.
     *
     * @param capacity The required minimum guest capacity. If null, the filter is ignored.
     * @return A Specification for the query checking for a capacity greater than or equal to the target value, or null.
     */
    public static Specification<Room> hasCapacity(Integer capacity){
        return ((root, query, criteriaBuilder) ->
                capacity == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("capacity"), capacity));
    }

    /**
     * Creates a specification to filter rooms within a specific nightly rate financial range.
     * Dynamically adjusts to handle lower bounds, upper bounds, or full range matches.
     *
     * @param minPrice The minimum nightly rate allowed. If null, no lower bound is enforced.
     * @param maxPrice The maximum nightly rate allowed. If null, no upper bound is enforced.
     * @return A Specification for the query evaluating price boundaries, or null if both parameters are null.
     */
    public static Specification<Room> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice){
        return ((root, query, criteriaBuilder) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return criteriaBuilder.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice);
            if (maxPrice == null) return criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice);
            return criteriaBuilder.between(root.get("pricePerNight"), minPrice, maxPrice);
        });
    }

    /**
     * Creates a specification to filter rooms that belong to a specific hotel.
     *
     * @param hotelId The unique identifier of the target hotel. If null, the filter is ignored.
     * @return A Specification for the query matching the parent hotel identifier, or null.
     */
    public static Specification<Room> belongsToHotel(UUID hotelId){
        return ((root, query, criteriaBuilder) ->
                hotelId == null ? null : criteriaBuilder.equal(root.get("hotel").get("id"), hotelId));
    }
}

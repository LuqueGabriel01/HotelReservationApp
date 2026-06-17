package com.hotelreservation.catalog.specifications;

import com.hotelreservation.catalog.models.entities.Amenity;
import com.hotelreservation.catalog.models.entities.Hotel;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class HotelSpecification {

    public static Specification<Hotel> hasCity(String city){
        return ((root, query, criteriaBuilder) ->
                city == null ? null : criteriaBuilder.equal(root.get("city"), city));
    }

    public static Specification<Hotel> hasStars(Integer stars){
        return ((root, query, criteriaBuilder) ->
                stars == null ? null : criteriaBuilder.equal(root.get("stars"), stars));
    }

    public static Specification<Hotel> hasAmenities(List<String> amenityNames){
        return ((root, query, criteriaBuilder) -> {
            if (amenityNames == null || amenityNames.isEmpty()) return null;

            query.distinct(true);

            Join<Hotel, Amenity> join = root.join("amenities");
            return join.get("name").in(amenityNames);
        });
    }
}

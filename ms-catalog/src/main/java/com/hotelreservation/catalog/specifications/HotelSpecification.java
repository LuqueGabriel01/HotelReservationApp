package com.hotelreservation.catalog.specifications;

import com.hotelreservation.catalog.models.entities.Amenity;
import com.hotelreservation.catalog.models.entities.Hotel;
import jakarta.persistence.criteria.Join;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Factory class providing reusable JPA Criteria API specifications for filtering Hotel entities.
 */
public class HotelSpecification {

  /**
   * Creates a specification to filter hotels by their city name.
   *
   * @param city The target city name. If null, the filter is ignored.
   * @return A Specification for the query matching the exact city name, or null.
   */
  public static Specification<Hotel> hasCity(String city) {
    return ((root, query, criteriaBuilder) ->
        city == null ? null : criteriaBuilder.equal(root.get("city"), city));
  }

  /**
   * Creates a specification to filter hotels by their star rating.
   *
   * @param stars The target star tier rating value. If null, the filter is ignored.
   * @return A Specification for the query matching the exact star count, or null.
   */
  public static Specification<Hotel> hasStars(Integer stars) {
    return ((root, query, criteriaBuilder) ->
        stars == null ? null : criteriaBuilder.equal(root.get("stars"), stars));
  }

  /**
   * Creates a specification to filter hotels that possess any of the specified amenities. Applies a
   * distinct modifier to prevent duplicate rows in the resulting join dataset.
   *
   * @param amenityNames A list of required amenity names. If empty or null, the filter is ignored.
   * @return A Specification that joins the amenities table and checks for matching names, or null.
   */
  public static Specification<Hotel> hasAmenities(List<String> amenityNames) {
    return ((root, query, criteriaBuilder) -> {
      if (amenityNames == null || amenityNames.isEmpty()) {
        return null;
      }

      query.distinct(true);

      Join<Hotel, Amenity> join = root.join("amenities");
      return join.get("name").in(amenityNames);
    });
  }
}

package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** Entity representing a hotel in the system. */
@Entity
@Table(
    name = "hotels",
    indexes = {@Index(name = "idx_hotel_city_stars", columnList = "city, stars")})
@Getter
@NoArgsConstructor
public class Hotel extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(length = 150)
  @Size(max = 150)
  private String description;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private String city;

  @Column(nullable = false)
  @Min(1)
  @Max(5)
  private int stars;

  @OneToMany(
      mappedBy = "hotel",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<Room> rooms = new ArrayList<>();

  @OneToMany(
      mappedBy = "hotel",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<ImageHotel> imagesHotel = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "hotel_amenities",
      joinColumns = @JoinColumn(name = "hotel_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id"))
  private List<Amenity> amenities = new ArrayList<>();

  /**
   * All-arguments domain constructor to initialize a new Hotel entity template instance.
   *
   * @param name Name of the hotel
   * @param description Short descriptive context summary
   * @param address Physical street address map location
   * @param city Target city geographical zone
   * @param stars Target tier count classification
   * @param amenities List of core services associated
   */
  public Hotel(
      String name,
      String description,
      String address,
      String city,
      int stars,
      List<Amenity> amenities) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.city = city;
    this.stars = stars;
    this.amenities = amenities;
  }

  /**
   * Business logic domain method to safely process partial modifications or dynamic updates onto
   * properties, while adding non-duplicate new amenities.
   *
   * @param name Optional updated name string
   * @param description Optional updated details content block
   * @param address Optional updated physical location point
   * @param city Optional updated municipality location group
   * @param stars Optional updated quality tier counter
   * @param amenities Optional target list of catalog reference points to merge
   */
  public void update(
      String name,
      String description,
      String address,
      String city,
      Integer stars,
      List<Amenity> amenities) {

    if (name != null) {
      this.name = name;
    }
    if (description != null) {
      this.description = description;
    }
    if (address != null) {
      this.address = address;
    }
    if (city != null) {
      this.city = city;
    }
    if (stars != null) {
      this.stars = stars;
    }
    if (amenities != null) {
      amenities.stream()
          .filter(amenity -> !this.amenities.contains(amenity))
          .forEach(this.amenities::add);
    }
  }
}

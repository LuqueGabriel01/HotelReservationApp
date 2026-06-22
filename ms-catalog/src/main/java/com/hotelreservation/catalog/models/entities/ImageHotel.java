package com.hotelreservation.catalog.models.entities;

import com.hotelreservation.catalog.models.entities.base.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/** Entity representing an image associated with a hotel. */
@Entity
@Table(name = "images_hotel")
@Getter
@NoArgsConstructor
public class ImageHotel extends AuditableEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "hotel_id", nullable = false)
  private Hotel hotel;

  @Column(nullable = false)
  @URL
  private String url;

  @Column(nullable = false)
  private boolean isMain = false;

  @Column private String publicId;

  /**
   * Constructs a new ImageHotel association entity instance.
   *
   * @param hotel the parent hotel entity reference
   * @param url the cloud network resource hosting string location
   * @param publicId the unique cloud storage vendor identifier asset id
   * @param isMain flag indicating if this image is the primary hotel asset display
   */
  public ImageHotel(Hotel hotel, String url, String publicId, boolean isMain) {
    this.hotel = hotel;
    this.url = url;
    this.publicId = publicId;
    this.isMain = isMain;
  }

  /** Sets this specific image state reference as the main view asset. */
  public void markAsMain() {
    this.isMain = true;
  }

  /** Clears this specific image state reference from being the main view asset. */
  public void unmarkAsMain() {
    this.isMain = false;
  }
}

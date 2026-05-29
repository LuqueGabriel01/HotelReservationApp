package com.hotelreservation.catalog.repositories;

import com.hotelreservation.catalog.models.entities.ImageHotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImageHotelRepository extends JpaRepository<ImageHotel, UUID> {
}

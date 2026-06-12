package com.hotelreservation.catalog.services;

import com.hotelreservation.catalog.repositories.AmenityRepository;
import com.hotelreservation.catalog.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;



}

package com.hotelreservation.availability.client;

import com.hotelreservation.availability.constants.ApiPaths;
import com.hotelreservation.availability.models.dtos.external.RoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CatalogClient {


    private final WebClient catalogWebClient;

    public List<RoomDto> getRoomsByHotel(UUID hotelId){
        return catalogWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ApiPaths.Catalog.ALL_ROOMS_BY_HOTEL_ID)
                        .build(hotelId))
                .retrieve()
                .bodyToFlux(RoomDto.class)
                .collectList()
                .block();
    }

}

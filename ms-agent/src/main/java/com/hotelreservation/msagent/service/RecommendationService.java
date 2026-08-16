package com.hotelreservation.msagent.service;

import com.hotelreservation.msagent.client.CatalogClient;
import com.hotelreservation.msagent.client.GeminiClient;
import com.hotelreservation.msagent.config.properties.GeminiProperties;
import com.hotelreservation.msagent.dto.catalog.RoomResponse;
import com.hotelreservation.msagent.dto.gemini.InteractionRequest;
import com.hotelreservation.msagent.dto.gemini.InteractionResponse;
import com.hotelreservation.msagent.dto.gemini.ResponseFormat;
import com.hotelreservation.msagent.dto.recommend.GeminiRecommendationItem;
import com.hotelreservation.msagent.dto.recommend.RecommendRequest;
import com.hotelreservation.msagent.dto.recommend.RoomRecommendation;
import com.hotelreservation.msagent.exception.AiServiceUnavailableException;
import com.hotelreservation.msagent.exception.NoAvailableRoomsException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/** Ranks a hotel's available rooms against a guest's stay requirements using Gemini. */
@Service
@AllArgsConstructor
public class RecommendationService {

  private final CatalogClient catalogClient;
  private final GeminiClient geminiClient;
  private final GeminiProperties geminiProperties;
  private final ObjectMapper objectMapper;

  /**
   * Recommends the best-suited rooms for a hotel based on the guest's stay requirements.
   *
   * @param request the hotel, guest count, budget, and preferences to base the recommendation on
   * @return a {@link Mono} emitting the ranked list of room recommendations
   * @throws NoAvailableRoomsException if ms-catalog has no rooms for the requested hotel
   * @throws AiServiceUnavailableException if Gemini does not return a usable recommendation
   */
  public Mono<List<RoomRecommendation>> recommend(RecommendRequest request) {
    return catalogClient
        .getRoomsByHotel(request.hotelId(), request.guests(), request.budget())
        .flatMap(
            rooms -> {
              if (rooms.isEmpty()) {
                return Mono.error(
                    new NoAvailableRoomsException("No available rooms were found for that hotel"));
              }
              return askGeminiToRank(request, rooms).map(items -> merge(items, rooms));
            });
  }

  private Mono<List<GeminiRecommendationItem>> askGeminiToRank(
      RecommendRequest request, List<RoomResponse> rooms) {
    String prompt = buildPrompt(request, rooms);
    InteractionRequest interactionRequest =
        new InteractionRequest(
            geminiProperties.model(),
            null,
            prompt,
            null,
            buildResponseFormat(),
            geminiProperties.toGenerationConfig(null));

    return geminiClient
        .createInteraction(interactionRequest)
        .map(this::extractModelOutputText)
        .map(this::parseRecommendations);
  }

  private String buildPrompt(RecommendRequest request, List<RoomResponse> rooms) {
    String roomsDescription =
        rooms.stream()
            .map(
                room ->
                    "roomId=%s, type=%s, capacity=%d, pricePerNight=%s, description=%s"
                        .formatted(
                            room.id(),
                            room.type(),
                            room.capacity(),
                            room.pricePerNight(),
                            room.description()))
            .collect(Collectors.joining("\n"));

    return """
        You are a hotel booking assistant. Recommend the best rooms \
        for a guest with these preferences:
        - Guests: %d
        - Budget: %s
        - Preferences: %s

        Available rooms:
        %s

        Return the rooms ordered by suitability (score 1 = best), \
        with a brief reason for each one.
        """
        .formatted(
            request.guests(),
            request.budget() != null ? request.budget() : "unspecified",
            request.preferences() != null ? request.preferences() : "no preferences specified",
            roomsDescription);
  }

  private ResponseFormat buildResponseFormat() {
    final String type = "type";
    Map<String, Object> itemSchema =
        Map.of(
            type,
            "object",
            "properties",
            Map.of(
                "roomId", Map.of(type, "string"),
                "score", Map.of(type, "integer"),
                "reason", Map.of(type, "string")),
            "required",
            List.of("roomId", "score", "reason"));

    Map<String, Object> schema =
        Map.of(
            "type",
            "object",
            "properties",
            Map.of("recommendations", Map.of("type", "array", "items", itemSchema)),
            "required",
            List.of("recommendations"));

    return new ResponseFormat("text", "application/json", schema);
  }

  private String extractModelOutputText(InteractionResponse response) {
    String text = response.modelOutputText();
    if (text.isBlank()) {
      throw new AiServiceUnavailableException("Gemini did not return any recommendation", null);
    }
    return text;
  }

  private List<GeminiRecommendationItem> parseRecommendations(String jsonText) {
    try {
      Map<String, List<GeminiRecommendationItem>> parsed =
          objectMapper.readValue(
              jsonText,
              objectMapper
                  .getTypeFactory()
                  .constructMapType(
                      Map.class,
                      objectMapper.getTypeFactory().constructType(String.class),
                      objectMapper
                          .getTypeFactory()
                          .constructCollectionType(List.class, GeminiRecommendationItem.class)));
      return parsed.getOrDefault("recommendations", List.of());
    } catch (JacksonException ex) {
      throw new AiServiceUnavailableException("Could not parse Gemini's response", ex);
    }
  }

  private List<RoomRecommendation> merge(
      List<GeminiRecommendationItem> items, List<RoomResponse> rooms) {
    Map<String, RoomResponse> roomsById =
        rooms.stream().collect(Collectors.toMap(RoomResponse::id, room -> room));

    return items.stream()
        .filter(item -> roomsById.containsKey(item.roomId()))
        .map(
            item -> {
              RoomResponse room = roomsById.get(item.roomId());
              return new RoomRecommendation(
                  room.id(),
                  room.type(),
                  room.pricePerNight(),
                  room.capacity(),
                  item.score(),
                  item.reason());
            })
        .toList();
  }
}

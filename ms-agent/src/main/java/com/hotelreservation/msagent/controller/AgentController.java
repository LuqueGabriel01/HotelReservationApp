package com.hotelreservation.msagent.controller;

import com.hotelreservation.msagent.constants.ApiPaths;
import com.hotelreservation.msagent.constants.OpenApiConstants;
import com.hotelreservation.msagent.dto.ErrorResponse;
import com.hotelreservation.msagent.dto.chat.ChatRequest;
import com.hotelreservation.msagent.dto.chat.ChatResponse;
import com.hotelreservation.msagent.dto.recommend.RecommendRequest;
import com.hotelreservation.msagent.dto.recommend.RecommendResponse;
import com.hotelreservation.msagent.service.ChatService;
import com.hotelreservation.msagent.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/** REST controller exposing the AI agent's chat and room recommendation endpoints. */
@RestController
@RequestMapping(ApiPaths.Agent.BASE_URL)
@AllArgsConstructor
@Tag(
    name = "AI Agent",
    description = "Endpoints for the hotel support chatbot and AI-powered room recommendations.")
public class AgentController {

  private final ChatService chatService;
  private final RecommendationService recommendationService;

  /**
   * Sends a guest message to the hotel support chatbot and returns its reply.
   *
   * @param request the chat message, hotel id, and optional session id to continue an existing
   *     conversation
   * @return a {@link Mono} emitting the chatbot's reply along with the session id
   */
  @Operation(
      summary = "Chat with the hotel support assistant",
      description =
          """
                    Sends a guest message to the Gemini-backed support chatbot and returns its \
                    reply. If no sessionId is provided, a new conversation is started; \
                    otherwise the conversation continues from the previous turn.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "The guest's message, target hotel, and optional session id.",
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ChatRequest.class)))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Chatbot reply generated successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ChatResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request payload.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SERVICE_UNAVAILABLE,
        description = "Gemini could not be reached or returned an error.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Agent.CHAT)
  public Mono<ResponseEntity<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
    String sessionId =
        (request.sessionId() == null || request.sessionId().isBlank())
            ? UUID.randomUUID().toString()
            : request.sessionId();

    return chatService
        .chat(sessionId, request.hotelId(), request.message())
        .map(reply -> new ChatResponse(sessionId, reply, Instant.now()))
        .map(ResponseEntity::ok);
  }

  /**
   * Recommends the best-suited rooms for a hotel based on the guest's stay requirements.
   *
   * @param request the hotel, dates, guest count, budget, and preferences to base the
   *     recommendation on
   * @return a {@link Mono} emitting the ranked list of room recommendations
   */
  @Operation(
      summary = "Get AI-ranked room recommendations",
      description =
          """
                    Fetches the available rooms for a hotel from ms-catalog and asks Gemini to \
                    rank them by suitability for the guest's stated preferences and budget.
                    """)
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "The hotel, stay dates, guest count, budget, and preferences.",
      required = true,
      content =
          @Content(
              mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = RecommendRequest.class)))
  @ApiResponses({
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SUCCESS,
        description = "Room recommendations generated successfully.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = RecommendResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.BAD_REQUEST,
        description = "Invalid request payload.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.NOT_FOUND,
        description = "No available rooms were found for that hotel.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))),
    @ApiResponse(
        responseCode = OpenApiConstants.Code.SERVICE_UNAVAILABLE,
        description = "ms-catalog or Gemini could not be reached or returned an error.",
        content =
            @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping(ApiPaths.Agent.RECOMMEND)
  public Mono<ResponseEntity<RecommendResponse>> recommend(
      @Valid @RequestBody RecommendRequest request) {
    return recommendationService
        .recommend(request)
        .map(RecommendResponse::new)
        .map(ResponseEntity::ok);
  }
}

package com.hotelreservation.msagent.dto.gemini;

/** A single piece of content (e.g. a text fragment) within an interaction {@link Step}. */
public record ContentPart(String type, String text) {}

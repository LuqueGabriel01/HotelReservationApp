package com.hotelreservation.msagent.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Centralized definition of API endpoint paths. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApiPaths {

  /** AI agent endpoints. */
  public static final class Agent {
    public static final String BASE_URL = "/api/ai";
    public static final String CHAT = "/chat";
    public static final String RECOMMEND = "/recommend";
  }
}

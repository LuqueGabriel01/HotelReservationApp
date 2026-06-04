package com.hotelreservation.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityConstants {

    public static final class Field {
        public static final String USERNAME = "username";
        public static final String ROLE = "role";
        public static final String BEARER = "Bearer ";
    }
}

package com.hotelreservation.availability.auth.enums;

import com.hotelreservation.availability.auth.models.entities.User;

/**
 * Represents the security and access roles available within the system.
 * <p>
 * These roles are used to control authorization and define the permissions
 * granted to a {@link User}.
 * </p>
 * @author Gabriel Luque
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
}

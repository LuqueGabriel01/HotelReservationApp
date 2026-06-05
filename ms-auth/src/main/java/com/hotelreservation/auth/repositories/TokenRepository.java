package com.hotelreservation.auth.repositories;

import com.hotelreservation.auth.models.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Repository interface for executing database operations on Token entities. */
public interface TokenRepository extends JpaRepository<Token, UUID> {

    /** Finds a token by its string value, eagerly fetching the associated user details. */
    @Query("SELECT t FROM Token t JOIN FETCH t.user WHERE t.token = :token")
    Optional<Token> findByToken(String token);

    /** Revokes and expires all active tokens for a specific user, marking the closure timestamp. */
    @Modifying
    @Transactional
    @Query("UPDATE Token t SET t.revoked = true, t.expired = true, t.closedAt = :now WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    void revokeAllActiveByUserId(@Param("userId") UUID userId, @Param("now") Instant now);
}

package org.example.userservice.repository;

import org.example.userservice.domain.TokenType;
import org.example.userservice.domain.User;
import org.example.userservice.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findByUser(User user);

    // Find valid tokens (not expired) for a specific user and type
    @Query("SELECT vt FROM VerificationToken vt WHERE vt.user = :user " +
            "AND vt.tokenType = :tokenType AND vt.expiryDate > :now AND vt.deleted = false")
    List<VerificationToken> findValidTokens(@Param("user") User user,
                                            @Param("tokenType") TokenType tokenType,
                                            @Param("now") LocalDateTime now);

    // Delete expired tokens
    @Query("UPDATE VerificationToken vt SET vt.deleted = true " +
            "WHERE vt.expiryDate < :now AND vt.deleted = false")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    // Find by token and type
    Optional<VerificationToken> findByTokenAndTokenType(String token, TokenType tokenType);

    // Check if user has any valid tokens of a specific type
    boolean existsByUserAndTokenTypeAndExpiryDateAfterAndDeletedFalse(User user,
                                                                      TokenType tokenType,
                                                                      LocalDateTime now);
}
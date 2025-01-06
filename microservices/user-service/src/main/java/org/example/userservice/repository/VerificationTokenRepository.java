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

    Optional<VerificationToken> findByTokenAndTokenTypeAndDeletedFalse(String token, TokenType tokenType);
}
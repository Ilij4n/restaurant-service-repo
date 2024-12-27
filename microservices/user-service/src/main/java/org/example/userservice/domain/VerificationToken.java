package org.example.userservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    // Definisemo samo jendu stranu veze, jer mislim da nam
    // Fetchovanje tokena po useru nece trebati, samo usera po tokenu za login
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "token_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    // Najobicnija java metoda za proveru da li je token istekao
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}

// Za slucaj da budemo zeleli da imamo vise vrsta tokena
enum TokenType {
    ACTIVATION,
    PASSWORD_RESET
}
package org.example.userservice.runner;

import org.example.userservice.domain.*;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.repository.VerificationTokenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Profile("default") // This ensures the runner only runs in the default profile
@Component
public class AppRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    public AppRunner(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    @Transactional // Important to ensure all operations happen in one transaction
    public void run(String... args) {
        System.out.println("Loading test data...");

        // Create an admin user
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("admin123"); // Remember: In production, this should be encoded
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setIsActive(true);
        admin = userRepository.save(admin);
        System.out.println("Created admin user with ID: " + admin.getId());

        // Create a client user
        Client client = new Client();
        client.setUsername("john_doe");
        client.setPassword("client123");
        client.setEmail("john@example.com");
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setDateOfBirth(LocalDate.of(1995, 5, 15));
        client.setReservationCount(5);
        client = userRepository.save(client);
        System.out.println("Created client user with ID: " + client.getId());

        // Create a restaurant manager
        RestaurantManager manager = new RestaurantManager();
        manager.setUsername("manager");
        manager.setPassword("manager123");
        manager.setEmail("manager@restaurant.com");
        manager.setFirstName("Restaurant");
        manager.setLastName("Manager");
        manager.setDateOfBirth(LocalDate.of(1985, 8, 20));
        manager.setEmploymentDate(LocalDate.of(2020, 1, 1));
        manager = userRepository.save(manager);
        System.out.println("Created manager user with ID: " + manager.getId());

        // Create verification tokens for the client (not activated yet)
        VerificationToken activationToken = new VerificationToken();
        activationToken.setToken(UUID.randomUUID().toString());
        activationToken.setUser(client);
        activationToken.setTokenType(TokenType.ACTIVATION);
        activationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(activationToken);
        System.out.println("Created activation token for client: " + activationToken.getToken());

        // Create a password reset token for the manager
        VerificationToken resetToken = new VerificationToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(manager);
        resetToken.setTokenType(TokenType.PASSWORD_RESET);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(2));
        verificationTokenRepository.save(resetToken);
        System.out.println("Created password reset token for manager: " + resetToken.getToken());

        // Test some repository methods
        System.out.println("\nTesting repository methods:");
        System.out.println("Finding user by email: " +
                userRepository.findByEmail("john@example.com").map(User::getUsername).orElse("Not found"));

        System.out.println("Finding active users: " +
                userRepository.findByIsActiveTrueAndDeletedFalse().size());

        System.out.println("Finding clients with minimum 5 reservations: " +
                userRepository.findClientsWithMinimumReservations(5).size());

        System.out.println("Finding valid tokens for client: " +
                verificationTokenRepository.findValidTokens(client, TokenType.ACTIVATION, LocalDateTime.now()).size());

        System.out.println("Test data loading completed!");
    }
}
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
import java.util.Random;
import java.util.UUID;

@Profile("default")
@Component
public class AppRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final Random random = new Random();

    public AppRunner(UserRepository userRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Loading test data...");

        // Keep existing test users
        // Admin user
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("admin123");
        admin.setEmail("admin@example.com");
        admin.setFirstName("Admin");
        admin.setLastName("User");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setIsActive(true);
        admin = userRepository.save(admin);
        System.out.println("Created admin user with ID: " + admin.getId());

        // Original test client
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

        // Original test manager
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

        // Create verification tokens for the original client
        VerificationToken activationToken = new VerificationToken();
        activationToken.setToken(UUID.randomUUID().toString());
        activationToken.setUser(client);
        activationToken.setTokenType(TokenType.ACTIVATION);
        activationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationTokenRepository.save(activationToken);

        // Create a password reset token for the original manager
        VerificationToken resetToken = new VerificationToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(manager);
        resetToken.setTokenType(TokenType.PASSWORD_RESET);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(2));
        verificationTokenRepository.save(resetToken);

        // Add multiple dummy clients
        System.out.println("Creating dummy clients...");
        String[] firstNames = {"Emma", "Liam", "Olivia", "Noah", "Ava", "Ethan", "Sophia", "Mason", "Isabella", "William"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez"};

        for (int i = 1; i <= 50; i++) {
            Client dummyClient = new Client();
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            dummyClient.setUsername(firstName.toLowerCase() + "_" + lastName.toLowerCase() + i);
            dummyClient.setPassword("password" + i);
            dummyClient.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@example.com");
            dummyClient.setFirstName(firstName);
            dummyClient.setLastName(lastName);
            dummyClient.setDateOfBirth(LocalDate.of(1980 + random.nextInt(25), 1 + random.nextInt(12), 1 + random.nextInt(28)));
            dummyClient.setIsActive(random.nextInt(100) < 80); // 80% chance of being active
            dummyClient.setIsBanned(random.nextInt(10) < 1); // 10% chance of being banned
            dummyClient.setReservationCount(random.nextInt(20)); // 0-19 reservations

            dummyClient = userRepository.save(dummyClient);
            //System.out.println("Created dummy client: " + dummyClient.getUsername() + " (Active: " + dummyClient.getIsActive() + ")");
        }

        // Add multiple dummy restaurant managers
        System.out.println("Creating dummy managers...");
        String[] restaurantNames = {
                "La Bella Italia", "The Golden Dragon", "Le Petit Bistro", "Taste of India",
                "The Hungry Bear", "Ocean's Delight", "The Cozy Corner", "Sushi Master",
                "The Spice Route", "Mediterranean Dreams"
        };

        for (int i = 1; i <= 50; i++) {
            RestaurantManager dummyManager = new RestaurantManager();
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];

            dummyManager.setUsername("manager" + firstName.toLowerCase() + i);
            dummyManager.setPassword("manager" + i);
            dummyManager.setEmail("manager" + i + "@" + restaurantNames[(i-1) % restaurantNames.length].toLowerCase().replace(" ", "") + ".com");
            dummyManager.setFirstName(firstName);
            dummyManager.setLastName(lastName);
            dummyManager.setDateOfBirth(LocalDate.of(1970 + random.nextInt(30), 1 + random.nextInt(12), 1 + random.nextInt(28)));
            dummyManager.setIsActive(random.nextInt(100) < 80); // 80% chance of being active
            dummyManager.setIsBanned(random.nextInt(20) < 1); // 5% chance of being banned
            dummyManager.setRestaurantName(restaurantNames[(i-1) % restaurantNames.length]);
            dummyManager.setEmploymentDate(LocalDate.of(2015 + random.nextInt(8), 1 + random.nextInt(12), 1 + random.nextInt(28)));

            dummyManager = userRepository.save(dummyManager);
            //System.out.println("Created dummy manager for restaurant: " + dummyManager.getRestaurantName() + " (Active: " + dummyManager.getIsActive() + ")");
        }

        // Create verification tokens for some dummy users
        createRandomVerificationTokens();

        System.out.println("Test data loading completed!");
    }

    private void createRandomVerificationTokens() {
        // Find some random inactive users and create activation tokens for them
        userRepository.findByIsActiveTrueAndDeletedFalse().stream()
                .limit(5)
                .forEach(user -> {
                    VerificationToken token = new VerificationToken();
                    token.setToken(UUID.randomUUID().toString());
                    token.setUser(user);
                    token.setTokenType(TokenType.ACTIVATION);
                    token.setExpiryDate(LocalDateTime.now().plusDays(1));
                    verificationTokenRepository.save(token);
                    //System.out.println("Created activation token for user: " + user.getUsername());
                });
    }
}
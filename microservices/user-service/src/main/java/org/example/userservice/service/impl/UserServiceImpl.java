package org.example.userservice.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.userservice.client.NotificationServiceClient;
import org.example.userservice.domain.*;
import org.example.userservice.dto.*;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.repository.VerificationTokenRepository;
import org.example.security.service.TokenService;
import org.example.userservice.repository.specification.UserSpecification;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Value("${oauth.jwt.secret}")
    private String jwtSecret;

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    // TokenService from security package, used to generate tokens (among other stuff)
    private final TokenService tokenService;
    // Added UserMapper for better separation of concerns
    private final UserMapper userMapper;
    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository, TokenService tokenService, UserMapper userMapper, NotificationServiceClient notificationServiceClient) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
        this.notificationServiceClient = notificationServiceClient;
    }

    @Override
    @Transactional
    public RegistrationResponseDto registerClient(UserRegisterDto dto) {
        // Check if user with given username and email exists
        validateRegistration(dto);

        Client client = userMapper.toClient(dto);
        // Persists the entry in the database
        client = userRepository.save(client);

        // Generate verification token
        VerificationToken verificationToken = createVerificationToken(client, TokenType.ACTIVATION);

        // Create and send activation notification
        SendNotificationRequestDto notificationRequest = new SendNotificationRequestDto();
        notificationRequest.setType("ACCOUNT_ACTIVATION");
        notificationRequest.setRecipientEmail("example@example.com"); // client.getEmail()
        // The template expects only one parameter - the activation URL
        notificationRequest.setParameters(List.of(String.format("http://localhost:8081/api/auth/verify/%s", verificationToken.getToken())));
        notificationRequest.setUserId(client.getId());

        // Send notification asynchronously
        notificationServiceClient.sendNotification(notificationRequest);

        // Returns an email (similar to id since unique) and a verification token to the front (made more sense so i changed it from a full UserDetailsDto)
        return RegistrationResponseDto.builder()
                .verificationToken(verificationToken.getToken())
                .email(client.getEmail())
                .message("Registration successful. Please check your email to verify your account.")
                .build();
    }

    @Override
    @Transactional
    public RegistrationResponseDto registerManager(UserRegisterDto dto) {
        // Same as in user registration
        validateRegistration(dto);
        RestaurantManager manager = userMapper.toManager(dto);

        manager = userRepository.save(manager);
        VerificationToken verificationToken = createVerificationToken(manager, TokenType.ACTIVATION);

        // Create and send activation notification - same logic as client registration
        SendNotificationRequestDto notificationRequest = new SendNotificationRequestDto();
        notificationRequest.setType("ACCOUNT_ACTIVATION");
        notificationRequest.setRecipientEmail("example@example.com"); // manager.getEmail()
        notificationRequest.setParameters(List.of(String.format("http://localhost:8081/api/auth/verify/%s", verificationToken.getToken())));
        notificationRequest.setUserId(manager.getId());

        // Send notification asynchronously
        notificationServiceClient.sendNotification(notificationRequest);

        // Returns an email (similar to id since unique) and a verification token to the front (made more sense so i changed it from a full UserDetailsDto)
        return RegistrationResponseDto.builder()
                .verificationToken(verificationToken.getToken())
                .email(manager.getEmail())
                .message("Registration successful. Please check your email to verify your account.")
                .build();
    }

    @Override
    public LoginResponseDto login(UserLoginDto dto) {
        // NOTE: The orElseThrow annotation is provided to us by the Optional class (which repo.findByEmail returns), allowing us to handle cases where database lookup returns none
        User user = userRepository.findByEmailAndDeletedFalse(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Fixed the password comparison logic - now throws when passwords don't match
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account not activated");
        }

        if (user.getIsBanned()) {
            throw new RuntimeException("Account is banned");
        }

        Claims claims = Jwts.claims();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        claims.put("email", user.getEmail());

        LoginResponseDto response = new LoginResponseDto();
        // Standard token generation
        response.setToken(tokenService.generate(claims));
        response.setTokenType("Bearer");

        // Return loginResponseDto with generated token
        return response;
    }

    @Override
    @Transactional
    // This occurs when a user clicks on a link provided in the email
    public MessageResponseDto verifyAccount(String token) {
        VerificationToken verificationToken = tokenRepository.findByTokenAndTokenTypeAndDeletedFalse(token, TokenType.ACTIVATION)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token expired");
        }
        // If token is valid, we update the user status and persist it in our database
        User user = verificationToken.getUser();
        user.setIsActive(true);
        // TIL that .save method of repo can be used for updating rows as well as inserting new ones
        userRepository.save(user);

        // Return affirmative answer to front
        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);
        response.setMessage("Account successfully verified");

        return response;
    }

    @Override
    // Fetches all data for a specific userId
    public UserDetailsDto getCurrentUserProfile(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDetailsDto updateProfile(Long userId, UpdateUserDto dto) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Malo nakaradna logika, ali sta da se radi...
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());

        // Update username if provided
        if (dto.getUsername() != null) {
            // Simple existence check - if anyone has this username, it's taken
            if (userRepository.existsByUsername(dto.getUsername())) {
                throw new RuntimeException("Username is already taken");
            }
            user.setUsername(dto.getUsername());
        }

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    // Admin only call
    public void updateUserBanStatus(Long userId, boolean banned) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBanned(banned);
        userRepository.save(user);
    }

    @Override
    // Will be implicitly invoked by the reservation service
    public ReservationCountDto getReservationCount(Long userId) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Client)) {
            throw new RuntimeException("User is not a client");
        }
        // Previse mi je bilo da pravim mapper za ovo
        ReservationCountDto dto = new ReservationCountDto();
        dto.setUserId(userId);
        dto.setCount(((Client) user).getReservationCount());
        return dto;
    }

    @Override
    @Transactional
    public ReservationCountDto updateReservationCount(Long userId, boolean increment) {
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Client)) {
            throw new RuntimeException("User is not a client");
        }

        Client client = (Client) user;
        if (increment) {
            client.setReservationCount(client.getReservationCount() + 1);
        } else {
            client.setReservationCount(Math.max(0, client.getReservationCount() - 1));
        }

        client = userRepository.save(client);

        // Previse mi je bilo da pravim mapper za ovo
        ReservationCountDto dto = new ReservationCountDto();
        dto.setUserId(userId);
        dto.setCount(client.getReservationCount());
        return dto;
    }

    @Override
    public Page<UserDetailsDto> searchUsers(UserFilterCriteria criteria, Pageable pageable) {
        // Create specification from criteria
        Specification<User> spec = UserSpecification.withFilterCriteria(criteria);

        // Execute search with pagination
        Page<User> userPage = userRepository.findAll(spec, pageable);

        // Map results to DTOs while preserving pagination
        return userPage.map(userMapper::toDto);
    }

    @Override
    @Transactional
    public MessageResponseDto initiateForgotPassword(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new RuntimeException("No account found with this email"));

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is not activated");
        }

        if (user.getIsBanned()) {
            throw new RuntimeException("Account is banned");
        }

        // Create password reset token
        VerificationToken resetToken = createVerificationToken(user, TokenType.PASSWORD_RESET);

        // Create notification request - dont wanna move this to Mapper honestly...
        SendNotificationRequestDto notificationRequest = new SendNotificationRequestDto();
        notificationRequest.setType("PASSWORD_RESET");
        notificationRequest.setRecipientEmail("example@example.com"); // Ponovo, ovo bi bio email arg, ali necu da spammujem random narod
        notificationRequest.setParameters(Arrays.asList(
                user.getFirstName(),
                String.format("http://localhost:5173/changepassword/id=%s", resetToken.getToken())
        ));

        // I dont think userId even gets used in our consumer
        notificationRequest.setUserId(user.getId());

        // Send notification asynchronously
        notificationServiceClient.sendNotification(notificationRequest);

        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);
        response.setMessage("Password reset instructions have been sent to your email");
        return response;
    }


    @Override
    @Transactional
    public MessageResponseDto resetPassword(String token, String newPassword) {
        VerificationToken resetToken = tokenRepository.findByTokenAndTokenTypeAndDeletedFalse(token, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(newPassword);
        userRepository.save(user);

        // Delete the used token
        resetToken.setDeleted(true);
        tokenRepository.save(resetToken);

        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);
        response.setMessage("Password has been successfully reset");
        return response;
    }


    // Helper methods
    private void validateRegistration(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
    }


    // Helper method to create verification token
    private VerificationToken createVerificationToken(User user, TokenType tokenType) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationToken.setTokenType(tokenType);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        return tokenRepository.save(verificationToken);
    }
}
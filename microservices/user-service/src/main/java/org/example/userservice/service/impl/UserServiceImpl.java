package org.example.userservice.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.userservice.domain.*;
import org.example.userservice.dto.*;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.repository.VerificationTokenRepository;
import org.example.security.service.TokenService;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    public UserServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository, TokenService tokenService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.tokenService = tokenService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserDetailsDto registerClient(UserRegisterDto dto) {
        // Check if user with given username and email exists
        validateRegistration(dto);

        // If not, create a new domain entity, with the fields given in dto
        Client client = new Client();
        // Does the thing stated above
        mapCommonUserFields(client, dto);
        client.setReservationCount(0);
        // Persists the entry in the database
        client = userRepository.save(client);

        // Generates and sends a verification token to the user - not yet fully implemented, notification-service does not exist yet!
        sendVerificationToken(client);

        // Returns a userDetails (extended register dto essentially) to the front
        // This one may contain more fields than necessary (such as isActive, isBanned and so on)... But we do not really care, we can just not process them at all
        return userMapper.toDto(client);
    }

    @Override
    @Transactional
    public UserDetailsDto registerManager(UserRegisterDto dto) {
        // Same as in user registration
        validateRegistration(dto);

        RestaurantManager manager = new RestaurantManager();
        mapCommonUserFields(manager, dto);
        manager.setRestaurantName(dto.getRestaurantName());
        manager.setEmploymentDate(dto.getEmploymentDate());

        manager = userRepository.save(manager);
        sendVerificationToken(manager);

        return userMapper.toDto(manager);
    }

    @Override
    public LoginResponseDto login(UserLoginDto dto) {
        // NOTE: The orElseThrow annotation is provided to us by the Optional class (which repo.findByEmail returns), allowing us to handle cases where database lookup returns none
        User user = userRepository.findByEmail(dto.getEmail())
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
        VerificationToken verificationToken = tokenRepository.findByTokenAndTokenType(token, TokenType.ACTIVATION)
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDetailsDto updateProfile(Long userId, UpdateUserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());

        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    // Admin only call
    public void updateUserBanStatus(Long userId, boolean banned) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBanned(banned);
        userRepository.save(user);
    }

    @Override
    // Will be implicitly invoked by the reservation service
    public ReservationCountDto getReservationCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!(user instanceof Client)) {
            throw new RuntimeException("User is not a client");
        }

        ReservationCountDto dto = new ReservationCountDto();
        dto.setUserId(userId);
        dto.setCount(((Client) user).getReservationCount());
        return dto;
    }

    @Override
    @Transactional
    public ReservationCountDto updateReservationCount(Long userId, boolean increment) {
        User user = userRepository.findById(userId)
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

        ReservationCountDto dto = new ReservationCountDto();
        dto.setUserId(userId);
        dto.setCount(client.getReservationCount());
        return dto;
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

    private void mapCommonUserFields(User user, UserRegisterDto dto) {
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // Ive decided to save the password as a plain string
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setIsActive(false);
        user.setIsBanned(false);
    }

    private void sendVerificationToken(User user) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setUser(user);
        verificationToken.setTokenType(TokenType.ACTIVATION);
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        tokenRepository.save(verificationToken);

        // TODO: Kontaktirati NotificationService da get url sa ovim tokenom (ili kao path varijablom ili kao request param) posalje odgovarajucem useru! Ovo cemo ostaviti za kasnije
    }
}
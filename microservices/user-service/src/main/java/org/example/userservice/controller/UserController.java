package org.example.userservice.controller;

import jakarta.validation.Valid;
import org.example.userservice.dto.*;
import org.example.userservice.security.CheckSecurity;
import org.example.userservice.domain.Role;
import org.example.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;
import org.example.userservice.security.service.TokenService;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    // Authentication endpoints
    @PostMapping("/auth/register/client")
    public ResponseEntity<UserDetailsDto> registerClient(@Valid @RequestBody UserRegisterDto dto) {
        // Simply delegate to service - validation is handled by @Valid annotation
        UserDetailsDto response = userService.registerClient(dto);
        // Just return an HTTP ok response with the corresponding dto (response argument)
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/register/manager")
    public ResponseEntity<UserDetailsDto> registerManager(@Valid @RequestBody UserRegisterDto dto) {
        UserDetailsDto response = userService.registerManager(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserLoginDto dto) {
        LoginResponseDto response = userService.login(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/verify/{token}")
    public ResponseEntity<MessageResponseDto> verifyAccount(@PathVariable String token) {
        MessageResponseDto response = userService.verifyAccount(token);
        return ResponseEntity.ok(response);
    }

    // User profile management
    @GetMapping("/users/profile")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER})
    public ResponseEntity<UserDetailsDto> getCurrentUserProfile(@RequestHeader("Authorization") String authorization) {
        // Extract userId from JWT token
        Claims claims = extractClaims(authorization);
        Long userId = claims.get("userId", Long.class);

        UserDetailsDto response = userService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/profile")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    public ResponseEntity<UserDetailsDto> updateProfile(@RequestHeader("Authorization") String authorization, @Valid @RequestBody UpdateUserDto dto) {
        Claims claims = extractClaims(authorization);
        Long userId = claims.get("userId", Long.class);

        UserDetailsDto response = userService.updateProfile(userId, dto);
        return ResponseEntity.ok(response);
    }

    // Admin functionalities
    @PutMapping("/users/{userId}/ban")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<Void> banUser(@RequestHeader("Authorization") String authorization, @PathVariable Long userId, @RequestParam Boolean banned) {
        userService.updateUserBanStatus(userId, banned);
        return ResponseEntity.ok().build();
    }

    // Reservation count management - used by other services
    @GetMapping("/users/reservations-count/{userId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    // Izvlacimo authorization is http headera u potpis metode Ovo je potrebno aspektu da bi proverio token i role (OK je sto u IDE-u pise da se ne koristi)
    public ResponseEntity<ReservationCountDto> getReservationCount(@RequestHeader("Authorization") String authorization, @PathVariable Long userId) {
        ReservationCountDto response = userService.getReservationCount(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/reservations-count/{userId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    public ResponseEntity<ReservationCountDto> updateReservationCount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateReservationCountDto dto) {
        ReservationCountDto response = userService.updateReservationCount(userId, dto.getIncrement());
        return ResponseEntity.ok(response);
    }

    // Helper method to extract claims from Authorization header
    private Claims extractClaims(String authorization) {
        String token = authorization.split(" ")[1]; // Remove "Bearer " prefix
        return tokenService.parseToken(token);
    }
}
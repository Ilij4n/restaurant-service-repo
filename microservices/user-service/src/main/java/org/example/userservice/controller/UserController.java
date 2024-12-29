package org.example.userservice.controller;

import jakarta.validation.Valid;
import org.example.userservice.dto.*;
import org.example.userservice.security.CheckSecurity;
import org.example.userservice.domain.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class UserController {

    // Prefix auth se po konvenciji koristi za sve endpointe koji imaju veze sa autentifikacijom (najcesce logini i registracije)
    @PostMapping("/auth/register/client")
    public ResponseEntity<UserDetailsDto> registerClient(@Valid @RequestBody UserRegisterDto dto) {
        // Will be implemented with service
        return null;
    }

    @PostMapping("/auth/register/manager")
    public ResponseEntity<UserDetailsDto> registerManager(@Valid @RequestBody UserRegisterDto dto) {
        // Will be implemented with service
        return null;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody UserLoginDto dto) {
        // Will be implemented with service
        return null;
    }

    @GetMapping("/auth/verify/{token}")
    public ResponseEntity<MessageResponseDto> verifyAccount(@PathVariable String token) {
        // Will be implemented with service
        return null;
    }

    // User profile management
    @GetMapping("/users/profile")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER})
    public ResponseEntity<UserDetailsDto> getCurrentUserProfile(@RequestHeader("Authorization") String authorization) {
        // Will be implemented with service
        return null;
    }

    @PutMapping("/users/profile")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    public ResponseEntity<UserDetailsDto> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateUserDto dto) {
        // Will be implemented with service
        return null;
    }

    // Admin functionalities
    @PutMapping("/users/{userId}/ban")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<Void> banUser(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long userId,
            @RequestParam Boolean banned) {
        // Will be implemented with service
        return null;
    }

    // Reservation count management - used by other services
    @GetMapping("/users/reservations-count/{userId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    public ResponseEntity<ReservationCountDto> getReservationCount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long userId) {
        // Will be implemented with service
        return null;
    }

    @PutMapping("/users/reservations-count/{userId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER, Role.ADMIN})
    public ResponseEntity<ReservationCountDto> updateReservationCount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateReservationCountDto dto) {
        // Will be implemented with service
        return null;
    }
}
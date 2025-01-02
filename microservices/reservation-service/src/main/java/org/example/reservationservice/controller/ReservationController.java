package org.example.reservationservice.controller;

import io.jsonwebtoken.Claims;
import org.example.reservationservice.dto.CreateReservationDto;
import org.example.reservationservice.dto.MessageResponseDto;
import org.example.reservationservice.dto.ReservationDto;
import org.example.reservationservice.dto.ReservationFilterCriteria;
import org.example.reservationservice.service.LoyaltyService;
import org.example.reservationservice.service.ReservationService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
import org.example.security.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

// Reservation management controller - handles actual reservations
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final TokenService tokenService;

    public ReservationController(ReservationService reservationService, TokenService tokenService) {
        this.reservationService = reservationService;
        this.tokenService = tokenService;
    }

    @PostMapping("/slot/{slotId}")
    @CheckSecurity(roles = {Role.CLIENT})
    public ResponseEntity<MessageResponseDto> createReservation(@RequestHeader("Authorization") String authorization, @PathVariable Long slotId, @Valid @RequestBody CreateReservationDto dto) {

        // Extract user information from JWT token
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        String userEmail = claims.get("email", String.class);
        Long userId = claims.get("userId", Long.class);

        // Pass both email and userId to service layer
        reservationService.createReservation(slotId, dto, userEmail, userId);

        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);
        response.setMessage("Your reservation has been successfully created. You will receive a confirmation email shortly.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reservationId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER})
    public ResponseEntity<MessageResponseDto> cancelReservation(@RequestHeader("Authorization") String authorization, @PathVariable Long reservationId) {

        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        String role = claims.get("role", String.class);
        String userEmail = claims.get("email", String.class);

        // Pass both role and email to service layer
        reservationService.cancelReservation(reservationId, Role.valueOf(role), userEmail);

        MessageResponseDto response = new MessageResponseDto();
        response.setSuccess(true);
        response.setMessage(Role.MANAGER.name().equals(role)
                ? "Reservation has been cancelled. The customer will be notified."
                : "Your reservation has been cancelled. The restaurant will be notified.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    @CheckSecurity(roles = {Role.CLIENT})
    public ResponseEntity<Page<ReservationDto>> getUserReservations(@RequestHeader("Authorization") String authorization, @ModelAttribute ReservationFilterCriteria criteria, Pageable pageable) {
        // Provide the dto ReservationDto aswell, we can again exclude the user id from it...
        // I assume its bad practice to have front send in any IDs at all, so we can avoid that by using the one from the token
        return ResponseEntity.ok(reservationService.getUserReservations(criteria, pageable));
    }
    // Dunno if this endpoint will get much use from the clients, though a search similar to this will most likely often happen in service layer business logic
    @GetMapping("/restaurant/{restaurantId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<Page<ReservationDto>> getRestaurantReservations(@RequestHeader("Authorization") String authorization, @PathVariable Long restaurantId, @ModelAttribute ReservationFilterCriteria criteria, Pageable pageable) {
        return ResponseEntity.ok(reservationService.getRestaurantReservations(restaurantId, criteria, pageable));
    }
}
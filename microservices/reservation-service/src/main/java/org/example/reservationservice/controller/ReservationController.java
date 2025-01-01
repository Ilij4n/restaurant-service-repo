package org.example.reservationservice.controller;

import org.example.reservationservice.service.ReservationService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
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

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/slot/{slotId}")
    @CheckSecurity(roles = {Role.CLIENT})
    public ResponseEntity<ReservationDto> createReservation(@RequestHeader("Authorization") String authorization, @PathVariable Long slotId, @Valid @RequestBody CreateReservationDto dto) {
        // Feel free to elaborate the need for separate dtos here aswell, if it isnt explicitly necessary, i would have one dto for this aswell
        return ResponseEntity.ok(reservationService.createReservation(slotId, dto));
    }

    @DeleteMapping("/{reservationId}")
    @CheckSecurity(roles = {Role.CLIENT, Role.MANAGER})
    public ResponseEntity<Void> cancelReservation(@RequestHeader("Authorization") String authorization, @PathVariable Long reservationId) {
        // I assume passing the role/id of the user cancelling the reservation would be nice here, if we opt into doing separate business logic for each case
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
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
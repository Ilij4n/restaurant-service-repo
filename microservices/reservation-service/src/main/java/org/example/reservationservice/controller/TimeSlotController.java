package org.example.reservationservice.controller;

import io.jsonwebtoken.Claims;
import org.example.reservationservice.dto.InputTimeSLotDto;
import org.example.reservationservice.dto.OutputTimeSlotDto;
import org.example.reservationservice.dto.TimeSlotFilterCriteria;
import org.example.reservationservice.service.TimeSlotService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
import org.example.security.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/slots")
public class TimeSlotController {
    private final TimeSlotService timeSlotService;
    private final TokenService tokenService;

    public TimeSlotController(TimeSlotService timeSlotService, TokenService tokenService) {
        this.timeSlotService = timeSlotService;
        this.tokenService = tokenService;
    }

    @PostMapping("/restaurant/{restaurantId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<OutputTimeSlotDto> createTimeSlot(@RequestHeader("Authorization") String authorization, @PathVariable Long restaurantId, @Valid @RequestBody InputTimeSLotDto dto) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);
        return ResponseEntity.ok(timeSlotService.createTimeSlot(restaurantId, managerId, dto));
    }

    @PutMapping("/{slotId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<OutputTimeSlotDto> updateTimeSlot(@RequestHeader("Authorization") String authorization, @PathVariable Long slotId, @Valid @RequestBody InputTimeSLotDto dto) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);
        return ResponseEntity.ok(timeSlotService.updateTimeSlot(slotId, managerId, dto));
    }

    @PutMapping("/{slotId}/visibility")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<OutputTimeSlotDto> updateSlotVisibility(@RequestHeader("Authorization") String authorization, @PathVariable Long slotId, @RequestParam Boolean visible) {
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);
        return ResponseEntity.ok(timeSlotService.updateSlotVisibility(slotId, managerId, visible));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<Page<OutputTimeSlotDto>> getRestaurantSlots(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long restaurantId,
            @ModelAttribute TimeSlotFilterCriteria criteria,
            Pageable pageable) {

        // Extract role and userId from JWT token
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Role userRole = Role.fromString(claims.get("role", String.class));
        Long userId = claims.get("userId", Long.class);

        // Pass the role and userId to service layer for role-specific handling
        return ResponseEntity.ok(timeSlotService.getRestaurantSlots(restaurantId, criteria, pageable, userRole, userId));
    }
}


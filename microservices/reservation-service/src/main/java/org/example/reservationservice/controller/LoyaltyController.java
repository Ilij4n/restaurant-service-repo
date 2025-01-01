package org.example.reservationservice.controller;

import org.example.reservationservice.service.LoyaltyService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

// Loyalty management controller - handles restaurant loyalty programs
@RestController
@RequestMapping("/api/loyalty")
public class LoyaltyController {
    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @PostMapping("/restaurant/{restaurantId}/perks")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<LoyaltyPerkDto> createLoyaltyPerk(@RequestHeader("Authorization") String authorization, @PathVariable Long restaurantId, @Valid @RequestBody CreateLoyaltyPerkDto dto) {
        // i dont see the need for having separate create and return dtos for loyalty perks, so we can do with a single one i guess
        return ResponseEntity.ok(loyaltyService.createLoyaltyPerk(restaurantId, dto));
    }

    @GetMapping("/restaurant/{restaurantId}/perks")
    public ResponseEntity<List<LoyaltyPerkDto>> getRestaurantPerks(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(loyaltyService.getRestaurantPerks(restaurantId));
    }

    @DeleteMapping("/perks/{perkId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<Void> deleteLoyaltyPerk(@RequestHeader("Authorization") String authorization, @PathVariable Long perkId) {
        loyaltyService.deleteLoyaltyPerk(perkId);
        return ResponseEntity.ok().build();
    }
}
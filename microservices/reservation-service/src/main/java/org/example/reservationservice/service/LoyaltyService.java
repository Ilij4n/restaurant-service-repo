package org.example.reservationservice.service;

import jakarta.validation.Valid;
import org.example.reservationservice.dto.LoyaltyPerkDto;

import java.util.List;

public interface LoyaltyService {
    LoyaltyPerkDto createLoyaltyPerk(Long restaurantId, @Valid LoyaltyPerkDto dto);

    List<LoyaltyPerkDto> getRestaurantPerks(Long restaurantId);

    void deleteLoyaltyPerk(Long perkId);
}

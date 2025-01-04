package org.example.reservationservice.mapper;

import org.example.reservationservice.domain.LoyaltyPerk;
import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.dto.LoyaltyPerkDto;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyPerkMapper {

    public LoyaltyPerk toEntity(LoyaltyPerkDto dto, Restaurant restaurant) {
        LoyaltyPerk perk = new LoyaltyPerk();
        perk.setRestaurant(restaurant);
        perk.setRequiredVisits(dto.getRequiredVisits());
        perk.setPerkDescription(dto.getPerkDescription());
        perk.setDiscountPercentage(dto.getDiscountPercentage());
        perk.setIsActive(true);
        return perk;
    }

    public void updateEntity(LoyaltyPerk perk, LoyaltyPerkDto dto) {
        if (dto.getRequiredVisits() != null) {
            perk.setRequiredVisits(dto.getRequiredVisits());
        }
        if (dto.getPerkDescription() != null) {
            perk.setPerkDescription(dto.getPerkDescription());
        }
        if (dto.getDiscountPercentage() != null) {
            perk.setDiscountPercentage(dto.getDiscountPercentage());
        }
        if (dto.getIsActive() != null) {
            perk.setIsActive(dto.getIsActive());
        }
    }

    public LoyaltyPerkDto toDto(LoyaltyPerk perk) {
        LoyaltyPerkDto dto = new LoyaltyPerkDto();
        dto.setId(perk.getId());
        dto.setRequiredVisits(perk.getRequiredVisits());
        dto.setPerkDescription(perk.getPerkDescription());
        dto.setDiscountPercentage(perk.getDiscountPercentage());
        dto.setRestaurantId(perk.getRestaurant().getId());
        dto.setRestaurantName(perk.getRestaurant().getName());
        dto.setIsActive(perk.getIsActive());
        return dto;
    }
}
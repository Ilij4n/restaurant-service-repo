package org.example.reservationservice.service.impl;

import org.example.reservationservice.dto.LoyaltyPerkDto;
import org.example.reservationservice.repository.LoyaltyPerkRepository;
import org.example.reservationservice.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyPerkRepository loyaltyPerkRepository;

    @Autowired
    public LoyaltyServiceImpl(LoyaltyPerkRepository loyaltyPerkRepository) {
        this.loyaltyPerkRepository = loyaltyPerkRepository;
    }

    @Override
    public LoyaltyPerkDto createLoyaltyPerk(Long restaurantId, LoyaltyPerkDto dto) {
        return null;
    }

    @Override
    public List<LoyaltyPerkDto> getRestaurantPerks(Long restaurantId) {
        return List.of();
    }

    @Override
    public void deleteLoyaltyPerk(Long perkId) {

    }
}

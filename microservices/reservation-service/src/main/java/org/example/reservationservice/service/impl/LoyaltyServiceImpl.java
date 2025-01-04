package org.example.reservationservice.service.impl;

import org.example.reservationservice.domain.LoyaltyPerk;
import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.dto.LoyaltyPerkDto;
import org.example.reservationservice.mapper.LoyaltyPerkMapper;
import org.example.reservationservice.repository.LoyaltyPerkRepository;
import org.example.reservationservice.repository.RestaurantRepository;
import org.example.reservationservice.service.LoyaltyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {
    private final LoyaltyPerkRepository loyaltyPerkRepository;
    private final RestaurantRepository restaurantRepository;
    private final LoyaltyPerkMapper loyaltyPerkMapper;

    public LoyaltyServiceImpl(LoyaltyPerkRepository loyaltyPerkRepository, RestaurantRepository restaurantRepository, LoyaltyPerkMapper loyaltyPerkMapper) {
        this.loyaltyPerkRepository = loyaltyPerkRepository;
        this.restaurantRepository = restaurantRepository;
        this.loyaltyPerkMapper = loyaltyPerkMapper;
    }

    @Override
    public LoyaltyPerkDto createLoyaltyPerk(Long restaurantId, LoyaltyPerkDto dto) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (loyaltyPerkRepository.existsByRestaurantIdAndRequiredVisitsAndDeletedFalse(
                restaurantId, dto.getRequiredVisits())) {
            throw new RuntimeException("A loyalty perk for this number of visits already exists");
        }

        LoyaltyPerk perk = loyaltyPerkMapper.toEntity(dto, restaurant);
        perk = loyaltyPerkRepository.save(perk);

        return loyaltyPerkMapper.toDto(perk);
    }

    @Override
    public List<LoyaltyPerkDto> getRestaurantPerks(Long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurant not found");
        }

        return loyaltyPerkRepository
                .findByRestaurantIdAndDeletedFalseOrderByRequiredVisitsAsc(restaurantId)
                .stream()
                .map(loyaltyPerkMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLoyaltyPerk(Long perkId) {
        LoyaltyPerk perk = loyaltyPerkRepository.findByIdAndDeletedFalse(perkId)
                .orElseThrow(() -> new RuntimeException("Loyalty perk not found"));

        perk.setDeleted(true);
        loyaltyPerkRepository.save(perk);
    }

    @Override
    public LoyaltyPerkDto updateLoyaltyPerk(Long perkId, LoyaltyPerkDto dto) {
        LoyaltyPerk perk = loyaltyPerkRepository.findByIdAndDeletedFalse(perkId)
                .orElseThrow(() -> new RuntimeException("Loyalty perk not found"));

        // If changing required visits, check for duplicates
        if (dto.getRequiredVisits() != null &&
                !dto.getRequiredVisits().equals(perk.getRequiredVisits())) {
            if (loyaltyPerkRepository.existsByRestaurantIdAndRequiredVisitsAndDeletedFalse(
                    perk.getRestaurant().getId(), dto.getRequiredVisits())) {
                throw new RuntimeException("A loyalty perk for this number of visits already exists");
            }
        }

        loyaltyPerkMapper.updateEntity(perk, dto);
        perk = loyaltyPerkRepository.save(perk);

        return loyaltyPerkMapper.toDto(perk);
    }
}


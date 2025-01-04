package org.example.reservationservice.repository;

import org.example.reservationservice.domain.LoyaltyPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LoyaltyPerkRepository extends JpaRepository<LoyaltyPerk, Long> {
    List<LoyaltyPerk> findByRestaurantIdAndDeletedFalseOrderByRequiredVisitsAsc(Long restaurantId);

    Optional<LoyaltyPerk> findByIdAndDeletedFalse(Long id);

    boolean existsByRestaurantIdAndRequiredVisitsAndDeletedFalse(Long restaurantId, Integer requiredVisits);

    // For checking if a perk has been achieved when making reservations
    Optional<LoyaltyPerk> findByRestaurantIdAndRequiredVisitsAndIsActiveTrueAndDeletedFalse(
            Long restaurantId, Integer requiredVisits);
}
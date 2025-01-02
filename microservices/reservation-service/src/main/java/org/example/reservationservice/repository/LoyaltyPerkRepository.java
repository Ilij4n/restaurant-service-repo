package org.example.reservationservice.repository;

import org.example.reservationservice.domain.LoyaltyPerk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoyaltyPerkRepository extends JpaRepository<LoyaltyPerk, Long> {
}

package org.example.reservationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// Loyalty perks that restaurants can define
@Entity
@Table(name = "loyalty_perks", indexes = {
        @Index(name = "idx_perk_restaurant", columnList = "restaurant_id"),
        @Index(name = "idx_perk_visits", columnList = "required_visits")
})
@Getter
@Setter
@NoArgsConstructor
public class LoyaltyPerk extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "required_visits", nullable = false)
    private Integer requiredVisits;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String perkDescription;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    // Flag to indicate if this perk is currently active
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
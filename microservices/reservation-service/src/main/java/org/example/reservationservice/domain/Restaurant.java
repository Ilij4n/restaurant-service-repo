package org.example.reservationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

// Main restaurant entity
@Entity
@Table(name = "restaurants", indexes = {
        @Index(name = "idx_restaurant_manager", columnList = "manager_id"),
        @Index(name = "idx_restaurant_cuisine", columnList = "cuisine_type")
})
@Getter
@Setter
@NoArgsConstructor
public class Restaurant extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "cuisine_type")
    private CuisineType cuisineType;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private Set<TimeSlot> timeSlots = new HashSet<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private Set<LoyaltyPerk> loyaltyPerks = new HashSet<>();
}
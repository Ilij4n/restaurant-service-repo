package org.example.reservationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Time slot entity representing a bookable table/space
@Entity
@Table(name = "time_slots", indexes = {
        @Index(name = "idx_slot_restaurant", columnList = "restaurant_id"),
        @Index(name = "idx_slot_status", columnList = "status"),
        @Index(name = "idx_slot_start_time", columnList = "start_time")
})
@Getter
@Setter
@NoArgsConstructor
public class TimeSlot extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @Column(name = "smoking_allowed")
    private Boolean smokingAllowed = false;

    @Column(name = "outdoor_seating")
    private Boolean outdoorSeating = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    // Bidirectional relationship with Reservation
    @OneToMany(mappedBy = "timeSlot", cascade = CascadeType.ALL)
    private Set<Reservation> reservations = new HashSet<>();
}
package org.example.reservationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

// Reservation entity linking users with time slots
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_email", columnList = "user_email"),  // Updated index
        @Index(name = "idx_reservation_restaurant", columnList = "restaurant_id"),
        @Index(name = "idx_reservation_status", columnList = "status"),
        @Index(name = "idx_reservation_notification", columnList = "notification_sent")
})
@Getter
@Setter
@NoArgsConstructor
public class Reservation extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    // Changed from userId to email
    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "restaurant_id", nullable = false)
    private Long restaurantId;

    @Column(name = "notification_sent")
    private Boolean notificationSent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String specialRequests;
}

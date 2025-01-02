package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.ReservationStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class ReservationFilterCriteria {
    // Time-based filters
    private LocalDateTime dateFrom;
    private LocalDateTime dateTo;

    // Status filters
    private ReservationStatus status;

    // User filters
    private String userEmail;  // Useful for finding specific user's reservations

    // Restaurant filters
    private Long restaurantId;
    private Set<Long> restaurantIds;  // For searching across multiple restaurants

    // Notification status
    private Boolean notificationSent;

    // Convenience filters
    private Boolean upcomingOnly;     // For future reservations
    private Boolean pastOnly;         // For historical reservations
    private Boolean activeOnly;       // Only ACTIVE status reservations

    // Additional filters for table characteristics
    private Boolean smokingArea;
    private Boolean outdoorSeating;

    // Helper methods to make the criteria more user-friendly
    public void addRestaurantId(Long id) {
        if (restaurantIds == null) {
            restaurantIds = new HashSet<>();
        }
        restaurantIds.add(id);
    }

    // Helper method to validate that conflicting filters aren't set
    public void validate() {
        if (Boolean.TRUE.equals(upcomingOnly) && Boolean.TRUE.equals(pastOnly)) {
            throw new IllegalArgumentException("Cannot set both upcomingOnly and pastOnly to true");
        }
    }
}
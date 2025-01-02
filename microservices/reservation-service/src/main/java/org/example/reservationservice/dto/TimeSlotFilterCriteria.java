package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.SlotStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class TimeSlotFilterCriteria {
    // Time-based filters
    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;

    // Capacity filter
    private Integer minSeatCount;
    private Integer maxSeatCount;

    // Table characteristics
    private Boolean smokingAllowed;
    private Boolean outdoorSeating;

    // Status filter
    private SlotStatus status;

    // Restaurant filtering
    private Set<Long> restaurantIds;  // Allow filtering by multiple restaurants

    // Helper method to add restaurant IDs incrementally if needed
    public void addRestaurantId(Long restaurantId) {
        if (restaurantIds == null) {
            restaurantIds = new HashSet<>();
        }
        restaurantIds.add(restaurantId);
    }
}
package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.SlotStatus;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class TimeSlotFilterCriteria {

    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;


    private Integer minSeatCount;
    private Integer maxSeatCount;


    private Boolean smokingAllowed;
    private Boolean outdoorSeating;

    // Status filter
    private SlotStatus status;

    // Internal flag to control visibility filtering
    private Boolean includeInvisible = false;

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
package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.CuisineType;
import java.time.LocalTime;

/**
 * Criteria for filtering restaurants in search operations.
 * Used with @ModelAttribute for GET requests, so all fields are optional.
 */
@Data
public class RestaurantFilterCriteria {
    private String name;              // For partial name matching
    private String location;          // For address/area searching
    private CuisineType cuisineType;  // For filtering by cuisine
    private Integer minSeats;         // Minimum available seats required
    private Boolean openNow;          // Filter for currently open restaurants
    private LocalTime targetTime;     // For checking availability at specific time
    private Boolean smokingAllowed;   // Filter for smoking/non-smoking areas
    private Boolean outdoorSeating;   // Filter for outdoor seating availability
}
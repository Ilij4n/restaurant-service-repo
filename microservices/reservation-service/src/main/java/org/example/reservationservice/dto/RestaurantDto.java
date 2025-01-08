package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.CuisineType;
import java.time.LocalTime;

/**
 * DTO for returning restaurant details. Includes all relevant information
 * about a restaurant, including its manager's email for contact purposes.
 */

@Data
public class RestaurantDto {
    private Long id;
    private String name;
    private String address;
    private String description;
    private CuisineType cuisineType;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private String managerEmail;  // Useful for contact information
    private Boolean isOpen;       // Calculated based on current time vs opening/closing times
    private Long totalSeats;      // Calculated from available time slots
}
package org.example.reservationservice.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.reservationservice.domain.CuisineType;
import java.time.LocalTime;

/**
 * DTO for updating an existing restaurant. All fields are optional to support
 * partial updates, similar to the user service's profile update functionality.
 */

@Data
public class UpdateRestaurantDto {
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;

    @Size(max = 200, message = "Address cannot exceed 200 characters")
    private String address;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private CuisineType cuisineType;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
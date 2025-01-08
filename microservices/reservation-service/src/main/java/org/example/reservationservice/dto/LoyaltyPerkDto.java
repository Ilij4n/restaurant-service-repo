package org.example.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Combined DTO for loyalty perks, used for both creation and display.
 * Includes validation for creation and additional fields for display.
 */
@Data
public class LoyaltyPerkDto {
    // Required fields for creation
    @NotNull(message = "Required number of visits cannot be null")
    @Min(value = 1, message = "Required visits must be at least 1")
    private Integer requiredVisits;

    @NotBlank(message = "Perk description is required")
    @Size(max = 500, message = "Perk description cannot exceed 500 characters")
    private String perkDescription;

    @Min(value = 0, message = "Discount percentage cannot be negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;

    // Optional fields used only in responses
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Boolean isActive;

    // User-specific information (populated when returning perks to clients)
    private Integer userCurrentVisits;    // Current visit count for the user
    private Boolean isAchieved;           // Whether user has reached this perk
    private Integer visitsUntilNextPerk;  // How many more visits needed
}
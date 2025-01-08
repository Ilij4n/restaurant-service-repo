package org.example.userservice.dto;

import lombok.Data;
import org.example.security.domain.Role;
import java.time.LocalDate;

@Data
public class UserFilterCriteria {
    private String username;        // For partial username matching
    private String email;           // For partial email matching
    private String firstName;       // For partial first name matching
    private String lastName;        // For partial last name matching
    private Role role;             // For filtering by role
    private Boolean isActive;       // For filtering by activation status
    private Boolean isBanned;       // For filtering by ban status
    private LocalDate joinedAfter;  // For filtering by creation date
    private LocalDate joinedBefore; // For filtering by creation date

    // Client-specific filters
    private Integer minReservations;  // Minimum number of reservations
    private Integer maxReservations;  // Maximum number of reservations

    // Manager-specific filters
    private String restaurantName;    // For partial restaurant name matching
}

package org.example.userservice.dto;

import lombok.Data;

@Data
public class UpdateReservationCountDto {
    // Could be changed to an integer value, in case we want to have more control over changing the user reservation count
    private Boolean increment;  // true to increment, false to decrement
}
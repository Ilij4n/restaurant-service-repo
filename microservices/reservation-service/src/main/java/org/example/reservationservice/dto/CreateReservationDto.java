package org.example.reservationservice.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * DTO for creating new reservations. Kept minimal since most information
 * comes from the JWT token and time slot. Only includes additional
 * information needed specifically for the reservation.
 */
@Data
public class CreateReservationDto {
    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
}

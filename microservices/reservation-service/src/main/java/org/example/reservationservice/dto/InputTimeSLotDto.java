package org.example.reservationservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InputTimeSLotDto {
    @NotNull(message = "Start time must be provided")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time must be provided")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;

    @NotNull(message = "Seat count must be provided")
    @Min(value = 1, message = "Seat count must be at least 1")
    private Integer seatCount;

    private Boolean smokingAllowed = false;
    private Boolean outdoorSeating = false;

    // Custom validation method that can be used by validator
    public void validate() {
        if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
            throw new RuntimeException("Start time must be before end time");
        }
    }
}
package org.example.reservationservice.dto;


import lombok.Data;
import org.example.reservationservice.domain.ReservationStatus;

import java.time.LocalDateTime;

// Ne verujem da cemo morati ista da saljemo kao potvrdu rezervacije timeSlota, ali od viska ugl glava ne boli
@Data
public class OutputTimeSlotDto {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer seatCount;
    private Boolean smokingAllowed;
    private Boolean outdoorSeating;
    private Boolean visible;
}
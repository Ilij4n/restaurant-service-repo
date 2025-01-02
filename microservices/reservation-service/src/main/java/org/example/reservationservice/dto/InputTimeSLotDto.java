package org.example.reservationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InputTimeSLotDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer seatCount;
    private Boolean smokingAllowed;
    private Boolean outdoorSeating;
}
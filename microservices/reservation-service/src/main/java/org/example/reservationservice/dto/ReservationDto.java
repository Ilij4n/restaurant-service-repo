package org.example.reservationservice.dto;

import lombok.Data;
import org.example.reservationservice.domain.ReservationStatus;

import java.time.LocalDateTime;
// Visak polja je siguran, ali su sva nullable pa je u redu
@Data
public class ReservationDto {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String specialRequests;
    private ReservationStatus status;
    private String userEmail;

    // Time slot related information
    private Integer seatCount;
    private Boolean smokingAllowed;
    private Boolean outdoorSeating;

    // Restaurant related information
    private Long restaurantId;
    private String restaurantName;
    private String restaurantAddress;

    // Derived fields for display
    private Boolean canBeCancelled;        // Based on time until reservation
    private Boolean needsReminder;         // Whether reminder should be sent
    private LocalDateTime cancelDeadline;  // Last time reservation can be cancelled
    private String tableDescription;       // e.g. "Outdoor non-smoking table for 4"
}
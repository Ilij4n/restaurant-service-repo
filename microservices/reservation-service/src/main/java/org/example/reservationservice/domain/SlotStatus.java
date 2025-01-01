package org.example.reservationservice.domain;

import lombok.Getter;

// Represents the current status of a time slot
@Getter
public enum SlotStatus {
    AVAILABLE("Available for booking"),
    RESERVED("Currently reserved"),
    MAINTENANCE("Under maintenance"),
    CANCELLED("Reservation cancelled");

    private final String description;

    SlotStatus(String description) {
        this.description = description;
    }
}

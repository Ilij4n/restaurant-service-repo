package org.example.reservationservice.domain;

import lombok.Getter;

// Status of a reservation
@Getter
public enum ReservationStatus {
    ACTIVE("Currently active"),
    COMPLETED("Visit completed"),
    CANCELLED_BY_USER("Cancelled by user"),
    CANCELLED_BY_RESTAURANT("Cancelled by restaurant"),
    NO_SHOW("Customer did not show up");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}
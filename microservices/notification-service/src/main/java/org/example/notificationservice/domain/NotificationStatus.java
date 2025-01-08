package org.example.notificationservice.domain;

import lombok.Getter;

@Getter
public enum NotificationStatus {
    PENDING("Waiting to be sent"),
    SENT("Successfully delivered"),
    FAILED("Delivery failed"),
    SCHEDULED("Scheduled for future delivery");

    private final String description;

    NotificationStatus(String description) {
        this.description = description;
    }
}
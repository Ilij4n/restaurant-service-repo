package org.example.notificationservice.domain;

import lombok.Getter;

// Enum for different types of cuisine
@Getter
public enum CuisineType {
    ITALIAN("Italian"),
    CHINESE("Chinese"),
    JAPANESE("Japanese"),
    SERBIAN("Serbian"),
    MEXICAN("Mexican"),
    INDIAN("Indian");

    private final String displayName;

    CuisineType(String displayName) {
        this.displayName = displayName;
    }

}
package org.example.notificationservice.dto;

import lombok.Data;
import org.example.notificationservice.domain.NotificationStatus;

import java.time.LocalDateTime;

/**
 * DTO for filtering notification history.
 * Used when searching through past notifications.
 */
@Data
public class NotificationFilterCriteria {
    private String type;
    private String recipientEmail;
    private Long restaurantId;
    private Long userId;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private NotificationStatus status;
}
package org.example.notificationservice.dto;

import lombok.Data;
import org.example.notificationservice.domain.NotificationStatus;
import java.time.LocalDateTime;

/**
 * DTO for viewing notification history.
 * Used when returning notification details to clients.
 */
@Data
public class NotificationHistoryDto {
    private Long id;
    private String type;
    private String recipientEmail;
    private LocalDateTime sentTime;
    private NotificationStatus status;
    private String renderedMessage;
    private Long restaurantId;
    private Long userId;
}
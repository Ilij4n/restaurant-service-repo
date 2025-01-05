package org.example.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * DTO for sending a new notification.
 * Used by other services when requesting a notification to be sent.
 */
@Data
public class SendNotificationRequestDto {
    @NotBlank(message = "Notification type is required")
    private String type;

    @NotBlank(message = "Recipient email is required")
    private String recipientEmail;

    @NotEmpty(message = "Parameters are required")
    private List<String> parameters;

    // Optional fields for tracking and filtering
    private Long restaurantId;
    private Long userId;
}
package org.example.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for creating and updating notification templates.
 * Used by admins to define new types of notifications.
 */
@Data
public class NotificationTemplateDto {
    private Long id;

    @NotBlank(message = "Notification type is required")
    private String type;

    @NotBlank(message = "Template text is required")
    private String templateText;

    // Parameter count is calculated automatically
    private Integer parameterCount;
}
package org.example.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// DTO for notification requests
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto {
    private String type;
    private String recipientEmail;
    private List<String> parameters;
    private Long restaurantId;
    private Long userId;
}
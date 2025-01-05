package org.example.reservationservice.mapper;

import org.example.reservationservice.domain.Reservation;
import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.NotificationRequestDto;
import org.example.security.domain.Role;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Component
public class NotificationRequestMapper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");

    public NotificationRequestDto toReservationConfirmationRequest(
            TimeSlot timeSlot,
            String userEmail,
            Long userId,
            String loyaltyMessage) {  // Added loyalty message parameter
        NotificationRequestDto request = new NotificationRequestDto();
        request.setType("RESERVATION_CONFIRMATION");
        request.setRecipientEmail(userEmail);
        request.setRestaurantId(timeSlot.getRestaurant().getId());
        request.setUserId(userId);

        // Parameters must be in the order expected by the template:
        // 1. User email
        // 2. Restaurant name
        // 3. Formatted date/time
        // 4. Loyalty message (can be empty string if no perks)
        request.setParameters(Arrays.asList(
                userEmail,
                timeSlot.getRestaurant().getName(),
                formatDateTime(timeSlot.getStartTime()),
                loyaltyMessage != null ? loyaltyMessage : ""  // Provide empty string if null
        ));

        return request;
    }

    public NotificationRequestDto toCancellationRequest(
            Reservation reservation,
            Role cancelerRole,
            String userEmail,
            Long userId,
            String managerEmail) {

        NotificationRequestDto request = new NotificationRequestDto();
        request.setRestaurantId(reservation.getRestaurantId());
        request.setUserId(userId);

        if (Role.MANAGER.equals(cancelerRole)) {
            // Manager cancelled - notify customer
            request.setType("RESERVATION_CANCELLED_BY_RESTAURANT");
            request.setRecipientEmail(userEmail);
            // Parameters: restaurant name, datetime
            request.setParameters(Arrays.asList(
                    reservation.getTimeSlot().getRestaurant().getName(),
                    formatDateTime(reservation.getTimeSlot().getStartTime())
            ));
        } else {
            // Customer cancelled - notify restaurant
            request.setType("RESERVATION_CANCELLED_BY_CUSTOMER");
            request.setRecipientEmail(managerEmail);
            // Parameters: customer email, datetime
            request.setParameters(Arrays.asList(
                    reservation.getUserEmail(),
                    formatDateTime(reservation.getTimeSlot().getStartTime())
            ));
        }

        return request;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
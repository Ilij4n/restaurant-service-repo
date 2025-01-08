package org.example.reservationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.reservationservice.client.NotificationServiceClient;
import org.example.reservationservice.domain.Reservation;
import org.example.reservationservice.domain.ReservationStatus;
import org.example.reservationservice.dto.NotificationRequestDto;
import org.example.reservationservice.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ReservationSchedulerService {
    private final ReservationRepository reservationRepository;
    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public ReservationSchedulerService(ReservationRepository reservationRepository, NotificationServiceClient notificationServiceClient) {
        this.reservationRepository = reservationRepository;
        this.notificationServiceClient = notificationServiceClient;
    }

    // Run every 30 minutes
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes in milliseconds - 30 * 60 * 1000
    @Transactional
    public void processReservations() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Starting scheduled reservation processing at {}", now);

        sendUpcomingReservationReminders(now);

        updateCompletedReservations(now);

        log.info("Completed scheduled reservation processing");
    }

    private void sendUpcomingReservationReminders(LocalDateTime now) {
        LocalDateTime oneDayFromNow = now.plusHours(24);

        List<Reservation> upcomingReservations = reservationRepository.findByStatusAndNotificationSentFalseAndTimeSlotStartTimeBetween(
                ReservationStatus.ACTIVE,
                now,
                oneDayFromNow
        );

        for (Reservation reservation : upcomingReservations) {
            try {
                // Create notification request
                NotificationRequestDto notificationRequest = new NotificationRequestDto();
                notificationRequest.setType("RESERVATION_REMINDER");
                notificationRequest.setRecipientEmail("example@example.com");   // Ovo bi bilo reservation.getUserEmail(),
                notificationRequest.setParameters(Arrays.asList(
                        reservation.getUserEmail(),
                        reservation.getTimeSlot().getRestaurant().getName(),
                        formatDateTime(reservation.getTimeSlot().getStartTime())
                ));
                notificationRequest.setRestaurantId(reservation.getRestaurantId());
                notificationRequest.setUserId(Long.parseLong(reservation.getUserId()));

                // Send reminder
                notificationServiceClient.sendNotification(notificationRequest);

                // Mark notification as sent
                reservation.setNotificationSent(true);
                reservationRepository.save(reservation);

                log.info("Sent reminder for reservation ID: {}", reservation.getId());
            } catch (Exception e) {
                log.error("Failed to send reminder for reservation ID: {}", reservation.getId(), e);
            }
        }
    }

    private void updateCompletedReservations(LocalDateTime now) {
        List<Reservation> completedReservations = reservationRepository
                .findByStatusAndTimeSlotEndTimeBefore(ReservationStatus.ACTIVE, now);

        for (Reservation reservation : completedReservations) {
            try {
                reservation.setStatus(ReservationStatus.COMPLETED);
                reservationRepository.save(reservation);
                log.info("Updated status to COMPLETED for reservation ID: {}", reservation.getId());
            } catch (Exception e) {
                log.error("Failed to update status for reservation ID: {}", reservation.getId(), e);
            }
        }
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm");
        return dateTime.format(formatter);
    }
}
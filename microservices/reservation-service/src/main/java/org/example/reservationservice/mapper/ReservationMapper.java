package org.example.reservationservice.mapper;

import org.example.reservationservice.domain.Reservation;
import org.example.reservationservice.domain.ReservationStatus;
import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.CreateReservationDto;
import org.example.reservationservice.dto.ReservationDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ReservationMapper {

    public Reservation toEntity(CreateReservationDto dto, TimeSlot timeSlot, String userEmail, Long userId) {
        Reservation reservation = new Reservation();
        reservation.setTimeSlot(timeSlot);
        reservation.setUserId(userId.toString());
        reservation.setUserEmail(userEmail);
        reservation.setRestaurantId(timeSlot.getRestaurant().getId());
        reservation.setSpecialRequests(dto.getSpecialRequests());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setNotificationSent(false);
        return reservation;
    }

    public ReservationDto toDto(Reservation reservation) {
        ReservationDto dto = new ReservationDto();
        dto.setId(reservation.getId());
        dto.setStartTime(reservation.getTimeSlot().getStartTime());
        dto.setEndTime(reservation.getTimeSlot().getEndTime());
        dto.setSpecialRequests(reservation.getSpecialRequests());
        dto.setStatus(reservation.getStatus());
        dto.setUserEmail(reservation.getUserEmail());

        // Time slot related information
        TimeSlot timeSlot = reservation.getTimeSlot();
        dto.setSeatCount(timeSlot.getSeatCount());
        dto.setSmokingAllowed(timeSlot.getSmokingAllowed());
        dto.setOutdoorSeating(timeSlot.getOutdoorSeating());

        // Restaurant related information
        dto.setRestaurantId(reservation.getRestaurantId());
        dto.setRestaurantName(timeSlot.getRestaurant().getName());
        dto.setRestaurantAddress(timeSlot.getRestaurant().getAddress());

        // Derived fields
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = timeSlot.getStartTime();

        // Can be cancelled up to 1 hour before reservation
        dto.setCanBeCancelled(startTime.isAfter(now.plusHours(1)));

        // Need reminder if starting in next 24 hours
        dto.setNeedsReminder(startTime.isAfter(now) && startTime.isBefore(now.plusHours(24)));

        // Set cancel deadline to 1 hour before start
        dto.setCancelDeadline(startTime.minusHours(1));

        // Create a descriptive string for the table
        // This one is funny
        StringBuilder tableDesc = new StringBuilder();
        tableDesc.append(timeSlot.getOutdoorSeating() ? "Outdoor" : "Indoor");
        tableDesc.append(timeSlot.getSmokingAllowed() ? " smoking" : " non-smoking");
        tableDesc.append(" table for ").append(timeSlot.getSeatCount());
        dto.setTableDescription(tableDesc.toString());

        return dto;
    }
}
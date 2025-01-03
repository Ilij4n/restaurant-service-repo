package org.example.reservationservice.service;

import jakarta.validation.Valid;
import org.example.reservationservice.dto.CreateReservationDto;
import org.example.reservationservice.dto.ReservationDto;
import org.example.reservationservice.dto.ReservationFilterCriteria;
import org.example.security.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationService {
    void createReservation(Long slotId, @Valid CreateReservationDto dto, String userEmail, Long userId);

    void cancelReservation(Long reservationId, Role role, String userEmail, Long userId);

    Page<ReservationDto> getUserReservations(ReservationFilterCriteria criteria, Pageable pageable);

    Page<ReservationDto> getRestaurantReservations(Long restaurantId, ReservationFilterCriteria criteria, Pageable pageable);
}

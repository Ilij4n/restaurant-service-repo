package org.example.reservationservice.service.impl;

import org.example.reservationservice.dto.CreateReservationDto;
import org.example.reservationservice.dto.ReservationDto;
import org.example.reservationservice.dto.ReservationFilterCriteria;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.service.ReservationService;
import org.example.security.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Override
    public void createReservation(Long slotId, CreateReservationDto dto, String userEmail, Long userId) {

    }

    @Override
    public void cancelReservation(Long reservationId, Role role, String userEmail) {

    }

    @Override
    public Page<ReservationDto> getUserReservations(ReservationFilterCriteria criteria, Pageable pageable) {
        return null;
    }

    @Override
    public Page<ReservationDto> getRestaurantReservations(Long restaurantId, ReservationFilterCriteria criteria, Pageable pageable) {
        return null;
    }
}

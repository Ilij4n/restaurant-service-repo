package org.example.reservationservice.repository;

import org.example.reservationservice.domain.Reservation;
import org.example.reservationservice.domain.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    Optional<Reservation> findByIdAndStatusAndDeletedFalse(Long id, ReservationStatus status);
}

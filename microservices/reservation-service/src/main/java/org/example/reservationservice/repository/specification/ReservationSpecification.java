package org.example.reservationservice.repository.specification;

import org.example.reservationservice.domain.Reservation;
import org.example.reservationservice.domain.ReservationStatus;
import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.ReservationFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationSpecification {

    public static Specification<Reservation> withFilterCriteria(ReservationFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted reservations
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Time-based filters
            if (criteria.getDateFrom() != null) {
                Join<Reservation, TimeSlot> timeSlotJoin = root.join("timeSlot");
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        timeSlotJoin.get("startTime"),
                        criteria.getDateFrom()
                ));
            }

            if (criteria.getDateTo() != null) {
                Join<Reservation, TimeSlot> timeSlotJoin = root.join("timeSlot");
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        timeSlotJoin.get("startTime"),
                        criteria.getDateTo()
                ));
            }

            // Status filter
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.getStatus()
                ));
            }

            // User filter
            if (criteria.getUserEmail() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userEmail"),
                        criteria.getUserEmail()
                ));
            }

            // Restaurant filters
            if (criteria.getRestaurantId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("restaurantId"),
                        criteria.getRestaurantId()
                ));
            }

            if (criteria.getRestaurantIds() != null && !criteria.getRestaurantIds().isEmpty()) {
                predicates.add(root.get("restaurantId").in(criteria.getRestaurantIds()));
            }

            // Notification status
            if (criteria.getNotificationSent() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("notificationSent"),
                        criteria.getNotificationSent()
                ));
            }

            // Convenience filters
            LocalDateTime now = LocalDateTime.now();
            Join<Reservation, TimeSlot> timeSlotJoin = root.join("timeSlot");

            if (Boolean.TRUE.equals(criteria.getUpcomingOnly())) {
                predicates.add(criteriaBuilder.greaterThan(
                        timeSlotJoin.get("startTime"),
                        now
                ));
            }

            if (Boolean.TRUE.equals(criteria.getPastOnly())) {
                predicates.add(criteriaBuilder.lessThan(
                        timeSlotJoin.get("startTime"),
                        now
                ));
            }

            if (Boolean.TRUE.equals(criteria.getActiveOnly())) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        ReservationStatus.ACTIVE
                ));
            }

            // Table characteristics
            if (criteria.getSmokingArea() != null) {
                predicates.add(criteriaBuilder.equal(
                        timeSlotJoin.get("smokingAllowed"),
                        criteria.getSmokingArea()
                ));
            }

            if (criteria.getOutdoorSeating() != null) {
                predicates.add(criteriaBuilder.equal(
                        timeSlotJoin.get("outdoorSeating"),
                        criteria.getOutdoorSeating()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
package org.example.reservationservice.repository.specification;

import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.TimeSlotFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotSpecification {

    public static Specification<TimeSlot> withFilterCriteria(TimeSlotFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always filter deleted slots
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Only add visibility filter for non-managers (when includeInvisible is false)
            // The following is basically a null safe version of just calling criteria.getIncludeInvisible()
            //  where the statement would just evaluate to false in case the field is null

            if (!Boolean.TRUE.equals(criteria.getIncludeInvisible())) {
                predicates.add(criteriaBuilder.equal(root.get("visible"), true));
            }

            // Filter by restaurant ID (required)
            if (criteria.getRestaurantIds() != null && !criteria.getRestaurantIds().isEmpty()) {
                predicates.add(root.get("restaurant").get("id").in(criteria.getRestaurantIds()));
            }

            // Add all optional filters only if they're provided
            if (criteria.getStartTimeFrom() != null) {
                // Only show future slots from the given time
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startTime"),
                        criteria.getStartTimeFrom()
                ));
            } else {
                // By default, only show future slots
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("startTime"),
                        LocalDateTime.now()
                ));
            }

            if (criteria.getStartTimeTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("startTime"),
                        criteria.getStartTimeTo()
                ));
            }

            if (criteria.getMinSeatCount() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("seatCount"),
                        criteria.getMinSeatCount()
                ));
            }

            if (criteria.getMaxSeatCount() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("seatCount"),
                        criteria.getMaxSeatCount()
                ));
            }

            if (criteria.getSmokingAllowed() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("smokingAllowed"),
                        criteria.getSmokingAllowed()
                ));
            }

            if (criteria.getOutdoorSeating() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("outdoorSeating"),
                        criteria.getOutdoorSeating()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
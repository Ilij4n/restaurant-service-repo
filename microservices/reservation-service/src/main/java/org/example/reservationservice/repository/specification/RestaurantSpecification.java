package org.example.reservationservice.repository.specification;

import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.dto.RestaurantFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSpecification {

    public static Specification<Restaurant> withFilterCriteria(RestaurantFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always include only non-deleted restaurants
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Apply name filter if provided (using partial match)
            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + criteria.getName().toLowerCase() + "%"
                ));
            }

            // Apply location filter if provided (using partial match)
            if (criteria.getLocation() != null && !criteria.getLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("address")),
                        "%" + criteria.getLocation().toLowerCase() + "%"
                ));
            }

            // Apply cuisine type filter if provided
            if (criteria.getCuisineType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("cuisineType"),
                        criteria.getCuisineType()
                ));
            }

            // If minSeats is provided, we'll need to join with TimeSlots
            // This is a more complex case that requires careful handling
            if (criteria.getMinSeats() != null) {
                query.distinct(true); // Prevent duplicates due to join
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.join("timeSlots").get("seatCount"),
                        criteria.getMinSeats()
                ));
            }

            // Combine all predicates with AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Restaurant> forManager(Long managerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("managerId"), managerId);
    }
}
package org.example.userservice.repository.specification;

import org.example.userservice.domain.Client;
import org.example.userservice.domain.RestaurantManager;
import org.example.userservice.domain.User;
import org.example.userservice.dto.UserFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> withFilterCriteria(UserFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted users
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Apply basic filters with prefix matching instead of substring matching
            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("username")),
                        criteria.getUsername().toLowerCase() + "%"  // Changed to prefix match
                ));
            }

            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        criteria.getEmail().toLowerCase() + "%"  // Changed to prefix match
                ));
            }

            if (criteria.getFirstName() != null && !criteria.getFirstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        criteria.getFirstName().toLowerCase() + "%"  // Changed to prefix match
                ));
            }

            if (criteria.getLastName() != null && !criteria.getLastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        criteria.getLastName().toLowerCase() + "%"  // Changed to prefix match
                ));
            }

            // Regular equality checks remain unchanged
            if (criteria.getRole() != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), criteria.getRole()));
            }

            if (criteria.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), criteria.getIsActive()));
            }

            if (criteria.getIsBanned() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isBanned"), criteria.getIsBanned()));
            }

            if (criteria.getJoinedAfter() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdAt").as(java.sql.Date.class),
                        java.sql.Date.valueOf(criteria.getJoinedAfter())
                ));
            }

            if (criteria.getJoinedBefore() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("createdAt").as(java.sql.Date.class),
                        java.sql.Date.valueOf(criteria.getJoinedBefore())
                ));
            }

            // Client-specific filters remain unchanged
            if (criteria.getMinReservations() != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.type(), Client.class),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("reservationCount"), criteria.getMinReservations())
                ));
            }

            if (criteria.getMaxReservations() != null) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.type(), Client.class),
                        criteriaBuilder.lessThanOrEqualTo(root.get("reservationCount"), criteria.getMaxReservations())
                ));
            }

            // Manager-specific filters with prefix matching
            if (criteria.getRestaurantName() != null && !criteria.getRestaurantName().isEmpty()) {
                predicates.add(criteriaBuilder.and(
                        criteriaBuilder.equal(root.type(), RestaurantManager.class),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("restaurantName")),
                                criteria.getRestaurantName().toLowerCase() + "%"  // Changed to prefix match
                        )
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
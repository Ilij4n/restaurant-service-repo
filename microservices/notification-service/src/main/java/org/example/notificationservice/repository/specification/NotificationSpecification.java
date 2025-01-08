package org.example.notificationservice.repository.specification;

import org.example.notificationservice.domain.NotificationInstance;
import org.example.notificationservice.dto.NotificationFilterCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class NotificationSpecification {

    public static Specification<NotificationInstance> withFilterCriteria(NotificationFilterCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always exclude deleted notifications
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));

            // Add type filter
            if (criteria.getType() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("template").get("type"),
                        criteria.getType()
                ));
            }

            // Add email filter with exact match
            if (criteria.getRecipientEmail() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("recipientEmail"),
                        criteria.getRecipientEmail()
                ));
            }

            // Add restaurant filter
            if (criteria.getRestaurantId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("restaurantId"),
                        criteria.getRestaurantId()
                ));
            }

            // Add user filter
            if (criteria.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("userId"),
                        criteria.getUserId()
                ));
            }

            // Add date range filters
            if (criteria.getFromDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("sentTime"),
                        criteria.getFromDate()
                ));
            }

            if (criteria.getToDate() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("sentTime"),
                        criteria.getToDate()
                ));
            }

            // Add status filter
            if (criteria.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("status"),
                        criteria.getStatus()
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
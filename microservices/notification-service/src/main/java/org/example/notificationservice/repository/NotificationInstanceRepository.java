package org.example.notificationservice.repository;

import org.example.notificationservice.domain.NotificationInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationInstanceRepository extends JpaRepository<NotificationInstance, Long>, JpaSpecificationExecutor<NotificationInstance> {
    // Basic CRUD operations are provided by JpaRepository
    // Complex queries will be handled by specifications
}
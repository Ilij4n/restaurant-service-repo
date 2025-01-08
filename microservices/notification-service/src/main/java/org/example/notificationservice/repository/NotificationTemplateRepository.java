package org.example.notificationservice.repository;


import jakarta.validation.constraints.NotBlank;
import org.example.notificationservice.domain.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    Optional<NotificationTemplate> findByType(String type);
    Optional<NotificationTemplate> findByIdAndDeletedFalse(Long id);
    boolean existsByTypeAndDeletedFalse(@NotBlank(message = "Notification type is required") String type);
    Optional<NotificationTemplate> findByTypeAndDeletedFalse(@NotBlank(message = "Notification type is required") String type);
}

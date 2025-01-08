package org.example.notificationservice.service;

import org.example.notificationservice.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {
    NotificationTemplateDto createTemplate(NotificationTemplateDto dto);

    NotificationTemplateDto updateTemplate(Long templateId, NotificationTemplateDto dto);

    void deleteTemplate(Long templateId);

    Page<NotificationTemplateDto> getTemplates(Pageable pageable);

    void sendNotification(SendNotificationRequestDto dto);

    Page<NotificationHistoryDto> getNotificationHistory(
            NotificationFilterCriteria criteria,
            Pageable pageable);

    Page<NotificationHistoryDto> getRestaurantNotifications(
            Long restaurantId,
            Long managerId,
            NotificationFilterCriteria criteria,
            String authToken,
            Pageable pageable);
}
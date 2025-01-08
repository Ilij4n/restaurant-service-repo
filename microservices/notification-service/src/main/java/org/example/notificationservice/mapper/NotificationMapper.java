package org.example.notificationservice.mapper;


import org.example.notificationservice.domain.NotificationTemplate;
import org.example.notificationservice.domain.NotificationInstance;
import org.example.notificationservice.dto.NotificationTemplateDto;
import org.example.notificationservice.dto.NotificationHistoryDto;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationTemplate toEntity(NotificationTemplateDto dto) {
        NotificationTemplate template = new NotificationTemplate();
        updateTemplateFromDto(template, dto);
        return template;
    }

    // This could have been kept in service layer business logic, but since its related to mapping, ive decided that it should be here
    public void updateTemplateFromDto(NotificationTemplate template, NotificationTemplateDto dto) {
        template.setType(dto.getType());
        template.setTemplateText(dto.getTemplateText());
        // Parameter count is handled by entity's PrePersist/PreUpdate
    }

    public NotificationTemplateDto toDto(NotificationTemplate template) {
        NotificationTemplateDto dto = new NotificationTemplateDto();
        dto.setId(template.getId());
        dto.setType(template.getType());
        dto.setTemplateText(template.getTemplateText());
        dto.setParameterCount(template.getParameterCount());
        return dto;
    }

    public NotificationHistoryDto toHistoryDto(NotificationInstance instance) {
        NotificationHistoryDto dto = new NotificationHistoryDto();
        dto.setId(instance.getId());
        dto.setType(instance.getTemplate().getType());
        dto.setRecipientEmail(instance.getRecipientEmail());
        dto.setSentTime(instance.getSentTime());
        dto.setStatus(instance.getStatus());
        dto.setRenderedMessage(instance.getRenderedMessage());
        dto.setRestaurantId(instance.getRestaurantId());
        dto.setUserId(instance.getUserId());
        return dto;
    }
}
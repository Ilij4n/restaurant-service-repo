package org.example.notificationservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.notificationservice.client.RestaurantServiceClient;
import org.example.notificationservice.domain.NotificationStatus;
import org.example.notificationservice.domain.NotificationTemplate;
import org.example.notificationservice.domain.NotificationInstance;
import org.example.notificationservice.dto.*;
import org.example.notificationservice.mapper.NotificationMapper;
import org.example.notificationservice.repository.NotificationTemplateRepository;
import org.example.notificationservice.repository.NotificationInstanceRepository;
import org.example.notificationservice.repository.specification.NotificationSpecification;
import org.example.notificationservice.service.EmailService;
import org.example.notificationservice.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationTemplateRepository templateRepository;
    private final NotificationInstanceRepository instanceRepository;
    private final NotificationMapper notificationMapper;
    private final RestaurantServiceClient restaurantServiceClient;
    private final EmailService emailService;

    public NotificationServiceImpl(
            NotificationTemplateRepository templateRepository,
            NotificationInstanceRepository instanceRepository,
            NotificationMapper notificationMapper,
            RestaurantServiceClient restaurantServiceClient,
            EmailService emailService) {
        this.templateRepository = templateRepository;
        this.instanceRepository = instanceRepository;
        this.notificationMapper = notificationMapper;
        this.restaurantServiceClient = restaurantServiceClient;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public NotificationTemplateDto createTemplate(NotificationTemplateDto dto) {
        // Check if template with this type already exists
        if (templateRepository.existsByTypeAndDeletedFalse(dto.getType())) {
            throw new RuntimeException("Template with this type already exists");
        }

        NotificationTemplate template = notificationMapper.toEntity(dto);
        template = templateRepository.save(template);
        return notificationMapper.toDto(template);
    }

    @Override
    @Transactional
    public NotificationTemplateDto updateTemplate(Long templateId, NotificationTemplateDto dto) {
        NotificationTemplate template = templateRepository.findByIdAndDeletedFalse(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        // Check if type is being changed and if new type already exists
        if (!template.getType().equals(dto.getType()) &&
                templateRepository.existsByTypeAndDeletedFalse(dto.getType())) {
            throw new RuntimeException("Template with new type already exists");
        }

        // Update all fields - there are not many fields for mapping from dto to domain, so i wont move this logic to the mapper
        notificationMapper.updateTemplateFromDto(template, dto);
        // Parameter count is automatically updated by entity

        template = templateRepository.save(template);
        return notificationMapper.toDto(template);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long templateId) {
        NotificationTemplate template = templateRepository.findByIdAndDeletedFalse(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        template.setDeleted(true);
        templateRepository.save(template);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplateDto> getTemplates(Pageable pageable) {
        // Returns the deleted notifications aswell, couldnt bother honestly
        return templateRepository.findAll(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationHistoryDto> getNotificationHistory(NotificationFilterCriteria criteria, Pageable pageable) {
        Specification<NotificationInstance> spec = NotificationSpecification.withFilterCriteria(criteria);

        return instanceRepository.findAll(spec, pageable).map(notificationMapper::toHistoryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationHistoryDto> getRestaurantNotifications(
            Long restaurantId,
            Long managerId,
            NotificationFilterCriteria criteria,
            String authToken,  // Add this parameter
            Pageable pageable) {

        // Verify manager has access to this restaurant
        if (!restaurantServiceClient.verifyManagerRestaurant(managerId, restaurantId, authToken)) {
            throw new RuntimeException("You don't have permission to view notifications for this restaurant");
        }

        // Set restaurant ID in criteria
        criteria.setRestaurantId(restaurantId);

        // Use specification for filtering
        Specification<NotificationInstance> spec = NotificationSpecification.withFilterCriteria(criteria);

        return instanceRepository.findAll(spec, pageable).map(notificationMapper::toHistoryDto);
    }


    @Override
    @Transactional
    public void sendNotification(SendNotificationRequestDto dto) {
        // Find the template
        NotificationTemplate template = templateRepository.findByTypeAndDeletedFalse(dto.getType())
                .orElseThrow(() -> new RuntimeException("Notification template not found: " + dto.getType()));

        // Validate parameter count
        if (dto.getParameters().size() != template.getParameterCount()) {
            throw new RuntimeException(String.format(
                    "Parameter count mismatch. Expected %d, got %d",
                    template.getParameterCount(),
                    dto.getParameters().size()
            ));
        }

        // Create notification instance - could be moved to mapper again
        NotificationInstance instance = new NotificationInstance();
        instance.setTemplate(template);
        instance.setRecipientEmail(dto.getRecipientEmail());
        instance.setRestaurantId(dto.getRestaurantId());
        instance.setUserId(dto.getUserId());
        instance.setParameters(dto.getParameters());

        // Render the message by replacing parameters
        String renderedMessage = renderMessage(template.getTemplateText(), dto.getParameters());
        instance.setRenderedMessage(renderedMessage);

        try {
            // Send the email
            emailService.sendEmail(
                    dto.getRecipientEmail(),
                    "Notification from Restaurant Reservation System", // You might want to make this configurable
                    renderedMessage
            );

            // Update status and sent time
            instance.setStatus(NotificationStatus.SENT);
            instance.setSentTime(LocalDateTime.now());
        } catch (Exception e) {
            instance.setStatus(NotificationStatus.FAILED);
            log.error("Failed to send email", e);
        }

        // Save the instance
        instanceRepository.save(instance);
    }

    private String renderMessage(String template, List<String> parameters) {
        String result = template;
        for (String parameter : parameters) {
            result = result.replaceFirst("%\\w+", parameter);
        }
        return result;
    }
}
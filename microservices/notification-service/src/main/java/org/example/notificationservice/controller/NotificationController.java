package org.example.notificationservice.controller;

import io.jsonwebtoken.Claims;
import org.example.notificationservice.dto.*;
import org.example.notificationservice.service.NotificationService;
import org.example.security.annotation.CheckSecurity;
import org.example.security.domain.Role;
import org.example.security.service.TokenService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final TokenService tokenService;

    public NotificationController(NotificationService notificationService, TokenService tokenService) {
        this.notificationService = notificationService;
        this.tokenService = tokenService;
    }

    // Template Management (Admin only)
    @PostMapping("/templates")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<NotificationTemplateDto> createTemplate(@RequestHeader("Authorization") String authorization, @Valid @RequestBody NotificationTemplateDto dto) {
        return ResponseEntity.ok(notificationService.createTemplate(dto));
    }
    // Partial updating of templates is not supported, so update it as a whole
    @PutMapping("/templates/{templateId}")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<NotificationTemplateDto> updateTemplate(@RequestHeader("Authorization") String authorization, @PathVariable Long templateId, @Valid @RequestBody NotificationTemplateDto dto) {
        // The id from the path parameter takes precedence over any id in the dto
        // This makes the API more predictable and prevents confusion
        NotificationTemplateDto updatedTemplate = notificationService.updateTemplate(templateId, dto);
        return ResponseEntity.ok(updatedTemplate);
    }

    @DeleteMapping("/templates/{templateId}")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<Void> deleteTemplate(@RequestHeader("Authorization") String authorization, @PathVariable Long templateId) {
        notificationService.deleteTemplate(templateId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/templates")
    @CheckSecurity(roles = {Role.ADMIN})
    public ResponseEntity<Page<NotificationTemplateDto>> getTemplates(@RequestHeader("Authorization") String authorization, Pageable pageable) {
        return ResponseEntity.ok(notificationService.getTemplates(pageable));
    }

    // Notification Sending Endpoint
    // This endpoint will be called by other services through message broker later
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody SendNotificationRequestDto dto) {
        notificationService.sendNotification(dto);
        return ResponseEntity.ok().build();
    }

    // Notification History Endpoints
    // Used by clients and admins, where admins have access to the full history, and clients only to the one related to them
    // Managers will use a restaurant based search defined below
    @GetMapping("/history")
    @CheckSecurity(roles = {Role.ADMIN, Role.CLIENT})
    public ResponseEntity<Page<NotificationHistoryDto>> getNotificationHistory(@RequestHeader("Authorization") String authorization, @ModelAttribute NotificationFilterCriteria criteria, Pageable pageable) {
        // Extract user information from JWT token
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Role userRole = Role.valueOf(claims.get("role", String.class));
        Long userId = claims.get("userId", Long.class);

        // For clients, we enforce filtering by their own userId
        if (Role.CLIENT.equals(userRole))
            criteria.setUserId(userId);  // Override any user ID in criteria for security

        // Admins can see all notifications, so we don't modify their criteria

        return ResponseEntity.ok(notificationService.getNotificationHistory(criteria, pageable));
    }

    // Additional endpoint for managers to view restaurant notifications
    @GetMapping("/history/restaurant/{restaurantId}")
    @CheckSecurity(roles = {Role.MANAGER})
    public ResponseEntity<Page<NotificationHistoryDto>> getRestaurantNotifications(@RequestHeader("Authorization") String authorization, @PathVariable Long restaurantId, @ModelAttribute NotificationFilterCriteria criteria, Pageable pageable) {
        // Extract manager ID from JWT token to verify restaurant association
        Claims claims = tokenService.parseToken(authorization.split(" ")[1]);
        Long managerId = claims.get("userId", Long.class);

        // Set restaurant ID in criteria and let service layer verify manager's association
        criteria.setRestaurantId(restaurantId);

        return ResponseEntity.ok(
                // FIXME AKO BUDE ZEZAO RESTAURANT CLIENT, VRATITI NA SAMO AUTHORIZATION KAO ARG
                notificationService.getRestaurantNotifications(restaurantId, managerId, criteria, authorization, pageable)
        );
    }
}
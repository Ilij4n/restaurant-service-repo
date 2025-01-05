package org.example.notificationservice.listener;

import org.example.notificationservice.dto.SendNotificationRequestDto;
import org.example.notificationservice.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationListener {
    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void handleNotificationRequest(SendNotificationRequestDto request) {
        try {
            log.info("Received notification request for type: {}", request.getType());
            notificationService.sendNotification(request);
        } catch (Exception e) {
            log.error("Failed to process notification request", e);
            // In a production environment, you might want to implement a dead letter queue
            // for failed messages
        }
    }
}

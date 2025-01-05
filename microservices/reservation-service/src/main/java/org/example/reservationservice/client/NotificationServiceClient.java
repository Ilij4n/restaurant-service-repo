package org.example.reservationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.example.reservationservice.dto.NotificationRequestDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationServiceClient {
    private final RabbitTemplate rabbitTemplate;
    private final String notificationExchange;
    private final String notificationRoutingKey;

    public NotificationServiceClient(RabbitTemplate rabbitTemplate, @Value("${notification.exchange}") String notificationExchange, @Value("${notification.routing-key}") String notificationRoutingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationExchange = notificationExchange;
        this.notificationRoutingKey = notificationRoutingKey;
    }

    public void sendNotification(NotificationRequestDto request) {
        try {
            log.info("Sending notification request for type: {}", request.getType());
            System.out.println("STIGAO DO OVDE!!!");
            rabbitTemplate.convertAndSend(notificationExchange, notificationRoutingKey, request);
        } catch (Exception e) {
            log.error("Failed to send notification request", e);
            // We might want to implement a retry mechanism here
        }
    }
}

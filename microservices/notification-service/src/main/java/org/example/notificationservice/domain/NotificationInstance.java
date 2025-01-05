package org.example.notificationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.notificationservice.domain.BaseEntity;
import org.example.notificationservice.domain.NotificationStatus;
import org.example.notificationservice.domain.NotificationTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notification_instances", indexes = {
        @Index(name = "idx_instance_recipient", columnList = "recipient_email"),
        @Index(name = "idx_instance_restaurant", columnList = "restaurant_id"),
        @Index(name = "idx_instance_sent_time", columnList = "sent_time"),
        @Index(name = "idx_instance_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class NotificationInstance extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private NotificationTemplate template;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @ElementCollection
    @CollectionTable(
            name = "notification_parameters",
            joinColumns = @JoinColumn(name = "notification_instance_id")
    )
    @OrderColumn(name = "parameter_order")
    @Column(name = "parameter_value")
    private List<String> parameters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status = NotificationStatus.PENDING;

    // The final rendered message after parameter substitution
    @Column(columnDefinition = "TEXT")
    private String renderedMessage;
}
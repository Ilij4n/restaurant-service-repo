package org.example.notificationservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notification_templates", indexes = {
        @Index(name = "idx_template_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
public class NotificationTemplate extends BaseEntity {
    // Removed the unique field, as it acted up when trying to create a new template with the same type of a previously deleted one
    // The uniqueness of a template type will instead be checked in service layer business logic
    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String templateText;

    @Column(name = "parameter_count", nullable = false)
    private Integer parameterCount;

    // Automatically calculate parameter count based on template text
    @PrePersist
    @PreUpdate
    private void calculateParameterCount() {
        // Count occurrences of %param in the template text
        this.parameterCount = (int) templateText.chars()
                .filter(ch -> ch == '%')
                .count();
    }

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL)
    private List<NotificationInstance> instances;
}






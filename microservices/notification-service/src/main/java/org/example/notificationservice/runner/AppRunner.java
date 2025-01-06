package org.example.notificationservice.runner;

import org.example.notificationservice.domain.NotificationTemplate;
import org.example.notificationservice.repository.NotificationTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AppRunner implements CommandLineRunner {
    private final NotificationTemplateRepository templateRepository;

    public AppRunner(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Creating notification templates...");

        // Account activation template
        createTemplate(
                "ACCOUNT_ACTIVATION",
                "Welcome to our Restaurant Reservation System!\n\n" +
                        "Please click the following link to activate your account:\n" +
                        "%activationUrl\n\n" +
                        "If you didn't create an account, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "Restaurant Reservation Team"
        );

        // Reservation confirmation template
        // Note: %loyaltyMessage can be empty string if no perks were earned
        createTemplate(
                "RESERVATION_CONFIRMATION",
                "Dear %name,\n\n" +
                        "Your reservation at %restaurant for %time has been confirmed.\n\n" +
                        "%loyaltyMessage\n\n" +  // New parameter for optional loyalty message
                        "We look forward to serving you!\n\n" +
                        "Best regards,\n" +
                        "Restaurant Reservation Team"
        );

        // Restaurant cancellation template
        createTemplate(
                "RESERVATION_CANCELLED_BY_RESTAURANT",
                "Dear Customer,\n\n" +
                        "We regret to inform you that your reservation at %restaurant for %time " +
                        "has been cancelled by the restaurant.\n\n" +
                        "We apologize for any inconvenience this may cause.\n\n" +
                        "Best regards,\n" +
                        "Restaurant Reservation Team"
        );

        // Customer cancellation template
        createTemplate(
                "RESERVATION_CANCELLED_BY_CUSTOMER",
                "Dear Restaurant Manager,\n\n" +
                        "A reservation has been cancelled by customer %customerEmail.\n\n" +
                        "Reservation details:\n" +
                        "Time: %time\n\n" +
                        "The time slot is now available for other customers.\n\n" +
                        "Best regards,\n" +
                        "Restaurant Reservation Team"
        );

        NotificationTemplate passwordResetTemplate = new NotificationTemplate();
        passwordResetTemplate.setType("PASSWORD_RESET");
        passwordResetTemplate.setTemplateText(
                "Hello %firstName,\n\n" +
                        "We received a request to reset your password. " +
                        "To reset your password, please click on the following link:\n\n" +
                        "%resetLink\n\n" +
                        "This link will expire in 2 hours. If you didn't request this, please ignore this email.\n\n" +
                        "Best regards,\nRestaurant Reservation System"
        );
        templateRepository.save(passwordResetTemplate);

        System.out.println("Notification templates created successfully!");
    }

    private void createTemplate(String type, String text) {
        if (!templateRepository.existsByTypeAndDeletedFalse(type)) {
            NotificationTemplate template = new NotificationTemplate();
            template.setType(type);
            template.setTemplateText(text);
            templateRepository.save(template);
            System.out.println("Created template: " + type);
        }
    }
}
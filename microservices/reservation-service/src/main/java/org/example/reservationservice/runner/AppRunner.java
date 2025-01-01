package org.example.reservationservice.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Profile("default") // This ensures the runner only runs in the default profile
@Component
public class AppRunner implements CommandLineRunner {

    @Override
    @Transactional // Important to ensure all operations happen in one transaction
    public void run(String... args) {

    }
}
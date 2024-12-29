package org.example.userservice.dto;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

// Ogromna output dto klasa koja ce se slati svim vrstama clienata preko GET metode (najvrv kad zatraze profile details ili tako nes)!
// Namerno sadrzi vise polja nego sto treba, jer su sva nullable i popunjavace se po potrebi
// Ona koja ne trebaju, samo ih u back kodu necemo popunjavati
// Koristi builder pattern jer mi je pao na pamet

/*
    Dodatan note - nacin invokeovanja builder patterna:

    UserDetailsDto clientDto = UserDetailsDto.builder()
    .email("test@test.com")
    .reservationCount(5)
    // no manager fields needed
    .build(

 */
@Data
@Builder
public class UserDetailsDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Boolean isActive;
    private Boolean isBanned;

    // Client-specific
    private Integer reservationCount;

    // Manager-specific
    private String restaurantName;
    private LocalDate employmentDate;
}

package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;

    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Username can only contain letters, numbers, underscores, and hyphens")
    private String username;

    // Note, promena maila nije dozvoljena, pa je izbacena

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
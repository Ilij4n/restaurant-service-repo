package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {
    private String firstName;
    private String lastName;

    // Note, promena maila nije dozvoljena
    @Email(message = "Please provide a valid email address")
    private String email;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
}
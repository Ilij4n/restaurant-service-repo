package org.example.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterDto {
    /*
       NOTE ZA VALIDACIJE
       Svaki dto primljen u kontroleru anotirati sa @Valid anotacijom
       Ovo daje mogucnost springu da automatski za nas posalje nazad
       "Bad request" odgovor useru, u slucaju da format dto-a nije dobar!
     */
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    // Manager-specific fields (nullable)
    private String restaurantName;
    @Past(message = "Date of employment must be in the past")
    private LocalDate employmentDate;
}
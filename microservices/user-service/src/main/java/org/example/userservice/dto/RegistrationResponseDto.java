package org.example.userservice.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RegistrationResponseDto {
    private String verificationToken;
    private String email;
    private String message;
}
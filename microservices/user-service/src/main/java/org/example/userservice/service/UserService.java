package org.example.userservice.service;

import org.example.userservice.dto.*;

public interface UserService {
    // Authentication methods
    RegistrationResponseDto registerClient(UserRegisterDto dto);
    RegistrationResponseDto registerManager(UserRegisterDto dto);
    LoginResponseDto login(UserLoginDto dto);
    MessageResponseDto verifyAccount(String token);

    // Profile management
    UserDetailsDto getCurrentUserProfile(Long userId);
    UserDetailsDto updateProfile(Long userId, UpdateUserDto dto);

    // Admin functionalities
    void updateUserBanStatus(Long userId, boolean banned);

    // Reservation count management
    ReservationCountDto getReservationCount(Long userId);
    ReservationCountDto updateReservationCount(Long userId, boolean increment);
}
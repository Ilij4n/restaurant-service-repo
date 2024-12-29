package org.example.userservice.mapper;

import org.example.userservice.domain.*;
import org.example.userservice.dto.UserDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDetailsDto toDto(User user) {
        UserDetailsDto.UserDetailsDtoBuilder builder = UserDetailsDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .isActive(user.getIsActive())
                .isBanned(user.getIsBanned());

        // Handle subclass-specific fields
        if (user instanceof Client) {
            builder.reservationCount(((Client) user).getReservationCount());
        } else if (user instanceof RestaurantManager) {
            RestaurantManager manager = (RestaurantManager) user;
            builder.restaurantName(manager.getRestaurantName())
                    .employmentDate(manager.getEmploymentDate());
        }

        return builder.build();
    }
}
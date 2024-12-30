package org.example.userservice.mapper;

import org.example.userservice.domain.*;
import org.example.userservice.dto.UserDetailsDto;
import org.example.userservice.dto.UserRegisterDto;
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

    public Client toClient(UserRegisterDto dto) {
        Client client = new Client();
        mapBaseFields(dto, client);
        client.setReservationCount(0);
        return client;
    }

    public RestaurantManager toManager(UserRegisterDto dto) {
        RestaurantManager manager = new RestaurantManager();
        mapBaseFields(dto, manager);
        manager.setRestaurantName(dto.getRestaurantName());
        manager.setEmploymentDate(dto.getEmploymentDate());
        return manager;
    }

    private void mapBaseFields(UserRegisterDto dto, User user) {
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setIsActive(false);
        user.setIsBanned(false);
    }
}
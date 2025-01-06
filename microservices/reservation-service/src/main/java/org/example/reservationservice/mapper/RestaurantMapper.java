package org.example.reservationservice.mapper;

import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.dto.CreateRestaurantDto;
import org.example.reservationservice.dto.RestaurantDto;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class RestaurantMapper {

    /**
     * Converts a CreateRestaurantDto to a Restaurant domain entity.
     * This method is used when creating new restaurants, setting only the initial fields
     * from the DTO and leaving other fields with their default values.
     *
     * @param dto The DTO containing the creation data
     * @param managerId The ID of the manager creating the restaurant
     * @return A new Restaurant entity
     */
    public Restaurant toEntity(CreateRestaurantDto dto, Long managerId) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.getName());
        restaurant.setAddress(dto.getAddress());
        restaurant.setDescription(dto.getDescription());
        restaurant.setCuisineType(dto.getCuisineType());
        restaurant.setOpeningTime(dto.getOpeningTime());
        restaurant.setClosingTime(dto.getClosingTime());
        restaurant.setManagerId(managerId);
        return restaurant;
    }

    /**
     * Converts a Restaurant domain entity to a RestaurantDto.
     * This method includes calculated fields like isOpen and totalSeats
     * which are computed based on current time and associated time slots.
     *
     * @param restaurant The Restaurant entity to convert
     * @return A RestaurantDto containing all relevant information
     */
    public RestaurantDto toDto(Restaurant restaurant) {
        RestaurantDto dto = new RestaurantDto();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setAddress(restaurant.getAddress());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setOpeningTime(restaurant.getOpeningTime());
        dto.setClosingTime(restaurant.getClosingTime());

        // Mozda settovati i manager email ovde...

        // Calculate if restaurant is currently open
        LocalTime currentTime = LocalTime.now();
        dto.setIsOpen(isRestaurantOpen(restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                currentTime));

        // Calculate total seats from available time slots
        dto.setTotalSeats(calculateTotalSeats(restaurant));

        return dto;
    }

    /**
     * Helper method to determine if a restaurant is currently open based on its hours.
     * Handles special cases like restaurants that close after midnight.
     */
    private boolean isRestaurantOpen(LocalTime openingTime, LocalTime closingTime, LocalTime currentTime) {
        if (openingTime == null || closingTime == null) {
            return false;
        }

        // Handle cases where closing time is after midnight
        if (closingTime.isBefore(openingTime)) {
            return currentTime.isAfter(openingTime) || currentTime.isBefore(closingTime);
        }

        return currentTime.isAfter(openingTime) && currentTime.isBefore(closingTime);
    }

    /**
     * Helper method to calculate the total number of seats in a restaurant
     * based on its time slots. This will be implemented later when time slot
     * functionality is added.
     */
    private Long calculateTotalSeats(Restaurant restaurant) {
        // This will be implemented once we have the TimeSlot functionality
        return 0L;
    }
}

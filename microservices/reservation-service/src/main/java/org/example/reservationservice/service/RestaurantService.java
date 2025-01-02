package org.example.reservationservice.service;

import jakarta.validation.Valid;
import org.example.reservationservice.dto.CreateRestaurantDto;
import org.example.reservationservice.dto.RestaurantDto;
import org.example.reservationservice.dto.RestaurantFilterCriteria;
import org.example.reservationservice.dto.UpdateRestaurantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RestaurantService {
    RestaurantDto createRestaurant(@Valid CreateRestaurantDto dto, Long managerId);

    RestaurantDto updateRestaurant(Long restaurantId, @Valid UpdateRestaurantDto dto, Long managerId);

    RestaurantDto getRestaurant(Long restaurantId);

    Page<RestaurantDto> searchRestaurants(RestaurantFilterCriteria criteria, Pageable pageable);

    Page<RestaurantDto> getManagerRestaurants(Long managerId, RestaurantFilterCriteria criteria, Pageable pageable);
}

package org.example.reservationservice.service.impl;

import org.example.reservationservice.dto.CreateRestaurantDto;
import org.example.reservationservice.dto.RestaurantDto;
import org.example.reservationservice.dto.RestaurantFilterCriteria;
import org.example.reservationservice.dto.UpdateRestaurantDto;
import org.example.reservationservice.repository.RestaurantRepository;
import org.example.reservationservice.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public RestaurantDto createRestaurant(CreateRestaurantDto dto) {
        return null;
    }

    @Override
    public RestaurantDto updateRestaurant(Long restaurantId, UpdateRestaurantDto dto) {
        return null;
    }

    @Override
    public RestaurantDto getRestaurant(Long restaurantId) {
        return null;
    }

    @Override
    public Page<RestaurantDto> searchRestaurants(RestaurantFilterCriteria criteria, Pageable pageable) {
        return null;
    }

    @Override
    public Page<RestaurantDto> getManagerRestaurants(Long managerId, RestaurantFilterCriteria criteria, Pageable pageable) {
        return null;
    }
}

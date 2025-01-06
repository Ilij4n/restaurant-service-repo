package org.example.reservationservice.service.impl;

import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.dto.CreateRestaurantDto;
import org.example.reservationservice.dto.RestaurantDto;
import org.example.reservationservice.dto.RestaurantFilterCriteria;
import org.example.reservationservice.dto.UpdateRestaurantDto;
import org.example.reservationservice.mapper.RestaurantMapper;
import org.example.reservationservice.repository.RestaurantRepository;
import org.example.reservationservice.repository.specification.RestaurantSpecification;
import org.example.reservationservice.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    // Core dependencies for restaurant operations
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    @Autowired
    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    @Override
    @Transactional
    public RestaurantDto createRestaurant(CreateRestaurantDto dto, Long managerId) {
        // Validate that the manager doesn't already have a restaurant with this name
        if (restaurantRepository.existsByManagerIdAndNameAndDeletedFalse(managerId, dto.getName())) {
            throw new RuntimeException("You already have a restaurant with this name");
        }

        // Convert DTO to entity using our mapper
        Restaurant restaurant = restaurantMapper.toEntity(dto, managerId);

        // Persist the new restaurant
        restaurant = restaurantRepository.save(restaurant);

        // Convert and return the created restaurant as DTO
        return restaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional
    public RestaurantDto updateRestaurant(Long restaurantId, UpdateRestaurantDto dto, Long managerId) {
        // Verify restaurant exists and belongs to the manager
        Restaurant restaurant = restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found or you don't have permission to modify it"));

        // Handle name update separately due to uniqueness constraint
        if (dto.getName() != null) {
            if (!dto.getName().equals(restaurant.getName()) &&
                    restaurantRepository.existsByManagerIdAndNameAndDeletedFalse(managerId, dto.getName())) {
                throw new RuntimeException("You already have a restaurant with this name");
            }
            restaurant.setName(dto.getName());
        }

        // Update other fields if provided
        if (dto.getAddress() != null) {
            restaurant.setAddress(dto.getAddress());
        }
        if (dto.getDescription() != null) {
            restaurant.setDescription(dto.getDescription());
        }
        if (dto.getCuisineType() != null) {
            restaurant.setCuisineType(dto.getCuisineType());
        }
        if (dto.getOpeningTime() != null) {
            restaurant.setOpeningTime(dto.getOpeningTime());
        }
        if (dto.getClosingTime() != null) {
            restaurant.setClosingTime(dto.getClosingTime());
        }

        // Save the updated entity
        restaurant = restaurantRepository.save(restaurant);

        // Convert and return the updated restaurant as DTO
        return restaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantDto getRestaurant(Long restaurantId) {
        // Find the restaurant by ID, ensuring it hasn't been soft-deleted
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Convert and return the found restaurant as DTO
        return restaurantMapper.toDto(restaurant);
    }

    @Override
    @Transactional(readOnly = true)
    // Za slucaj da zatreba - Kolko sam video, parametri za paging se mogu proslediti kao najobicniji request parametri, ovako otp:
    // GET /api/restaurants?page=0&size=10&sort=name,desc&sort=cuisineType,asc
    public Page<RestaurantDto> searchRestaurants(RestaurantFilterCriteria criteria, Pageable pageable) {
        // Create a specification based on the filter criteria
        Specification<Restaurant> spec = RestaurantSpecification.withFilterCriteria(criteria);

        // Execute the search with pagination
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);

        // Map the results to DTOs while preserving pagination information
        return restaurantPage.map(restaurantMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RestaurantDto> getManagerRestaurants(Long managerId, RestaurantFilterCriteria criteria, Pageable pageable) {
        // Combine manager-specific specification with filter criteria
        Specification<Restaurant> spec = Specification
                .where(RestaurantSpecification.forManager(managerId))
                .and(RestaurantSpecification.withFilterCriteria(criteria));

        // Execute the search with pagination
        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);

        // Map the results to DTOs while preserving pagination information
        return restaurantPage.map(restaurantMapper::toDto);
    }
}
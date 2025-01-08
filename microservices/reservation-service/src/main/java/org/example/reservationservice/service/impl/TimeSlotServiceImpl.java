package org.example.reservationservice.service.impl;

import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.InputTimeSLotDto;
import org.example.reservationservice.dto.OutputTimeSlotDto;
import org.example.reservationservice.dto.TimeSlotFilterCriteria;
import org.example.reservationservice.mapper.TimeSlotMapper;
import org.example.reservationservice.repository.RestaurantRepository;
import org.example.reservationservice.repository.TimeSlotRepository;
import org.example.reservationservice.repository.specification.TimeSlotSpecification;
import org.example.reservationservice.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.security.domain.Role;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;
    private final RestaurantRepository restaurantRepository;
    private final TimeSlotMapper timeSlotMapper;

    @Autowired
    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository, RestaurantRepository restaurantRepository, TimeSlotMapper timeSlotMapper) {
        this.timeSlotRepository = timeSlotRepository;
        this.restaurantRepository = restaurantRepository;
        this.timeSlotMapper = timeSlotMapper;
    }

    @Override
    @Transactional
    public OutputTimeSlotDto createTimeSlot(Long restaurantId, Long managerId, InputTimeSLotDto dto) {
        // Verify restaurant exists and belongs to the manager
        Restaurant restaurant = restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found or you don't have permission to modify it"));

        // Validate time slot against restaurant hours
        validateRestaurantHours(dto, restaurant);

        // Run DTO's internal validation
        dto.validate();

        // Convert to domain object and save
        TimeSlot timeSlot = timeSlotMapper.toDomain(dto, restaurant);
        timeSlot = timeSlotRepository.save(timeSlot);

        return timeSlotMapper.toDto(timeSlot);
    }

    @Override
    @Transactional
    public OutputTimeSlotDto updateTimeSlot(Long slotId, Long managerId, InputTimeSLotDto dto) {
        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (!timeSlot.getRestaurant().getManagerId().equals(managerId)) {
            throw new RuntimeException("You don't have permission to modify this time slot");
        }

        // Validate time slot against restaurant hours
        validateRestaurantHours(dto, timeSlot.getRestaurant());

        // Run DTO's internal validation
        dto.validate();

        // Update domain object with partial update support
        timeSlotMapper.updateDomain(timeSlot, dto);
        timeSlot = timeSlotRepository.save(timeSlot);

        return timeSlotMapper.toDto(timeSlot);
    }

    @Override
    @Transactional
    public OutputTimeSlotDto updateSlotVisibility(Long slotId, Long managerId, Boolean visible) {
        TimeSlot timeSlot = timeSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Time slot not found"));

        if (!timeSlot.getRestaurant().getManagerId().equals(managerId)) {
            throw new RuntimeException("You don't have permission to modify this time slot");
        }

        timeSlot.setVisible(visible);
        timeSlot = timeSlotRepository.save(timeSlot);

        return timeSlotMapper.toDto(timeSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OutputTimeSlotDto> getRestaurantSlots(
            Long restaurantId,
            TimeSlotFilterCriteria criteria,
            Pageable pageable,
            Role userRole,
            Long userId) {

        // Verify restaurant exists
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Initialize criteria if null
        if (criteria == null) {
            criteria = new TimeSlotFilterCriteria();
        }

        // Add restaurant ID to criteria
        criteria.addRestaurantId(restaurantId);

        // Handle role-specific logic
        if (Role.MANAGER.equals(userRole)) {
            // For managers, verify ownership and show all slots
            if (!restaurant.getManagerId().equals(userId)) {
                throw new RuntimeException("You don't have permission to view all slots for this restaurant");
            }
            // Manager can see all slots, so we'll use a specification without visibility filter
            criteria.setIncludeInvisible(true);
        }
        // For clients, we don't need to do anything special as the specification will handle visibility filtering by default

        // Create and execute specification
        Specification<TimeSlot> spec = TimeSlotSpecification.withFilterCriteria(criteria);
        return timeSlotRepository.findAll(spec, pageable)
                .map(timeSlotMapper::toDto);
    }

    private void validateRestaurantHours(InputTimeSLotDto dto, Restaurant restaurant) {
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return; // Let the DTO validation handle this
        }

        java.time.LocalTime slotStartTime = dto.getStartTime().toLocalTime();
        java.time.LocalTime slotEndTime = dto.getEndTime().toLocalTime();

        if (slotStartTime.isBefore(restaurant.getOpeningTime()) || slotEndTime.isAfter(restaurant.getClosingTime())) {
            throw new RuntimeException("Time slot must be within restaurant operating hours");
        }
    }
}
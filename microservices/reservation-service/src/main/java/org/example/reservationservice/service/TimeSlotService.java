package org.example.reservationservice.service;

import jakarta.validation.Valid;
import org.example.reservationservice.dto.InputTimeSLotDto;
import org.example.reservationservice.dto.OutputTimeSlotDto;
import org.example.reservationservice.dto.TimeSlotFilterCriteria;
import org.example.security.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimeSlotService {
    OutputTimeSlotDto createTimeSlot(Long restaurantId, Long managerId, @Valid InputTimeSLotDto dto);

    OutputTimeSlotDto updateTimeSlot(Long slotId, Long managerId, @Valid InputTimeSLotDto dto);

    OutputTimeSlotDto updateSlotVisibility(Long slotId, Long managerId, Boolean visible);

    Page<OutputTimeSlotDto> getRestaurantSlots(Long restaurantId, TimeSlotFilterCriteria criteria, Pageable pageable, Role userRole, Long userId);
}

package org.example.reservationservice.service.impl;

import org.example.reservationservice.dto.InputTimeSLotDto;
import org.example.reservationservice.dto.OutputTimeSlotDto;
import org.example.reservationservice.dto.TimeSlotFilterCriteria;
import org.example.reservationservice.repository.ReservationRepository;
import org.example.reservationservice.repository.TimeSlotRepository;
import org.example.reservationservice.service.TimeSlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    public TimeSlotServiceImpl(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    @Override
    public OutputTimeSlotDto createTimeSlot(Long restaurantId, Long managerId, InputTimeSLotDto dto) {
        return null;
    }

    @Override
    public OutputTimeSlotDto updateTimeSlot(Long slotId, Long managerId, InputTimeSLotDto dto) {
        return null;
    }

    @Override
    public OutputTimeSlotDto updateSlotVisibility(Long slotId, Long managerId, Boolean visible) {
        return null;
    }

    @Override
    public Page<OutputTimeSlotDto> getRestaurantSlots(Long restaurantId, TimeSlotFilterCriteria criteria, Pageable pageable) {
        return null;
    }
}

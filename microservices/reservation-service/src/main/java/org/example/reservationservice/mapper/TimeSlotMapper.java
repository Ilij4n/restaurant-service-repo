package org.example.reservationservice.mapper;

import org.example.reservationservice.domain.Restaurant;
import org.example.reservationservice.domain.TimeSlot;
import org.example.reservationservice.dto.InputTimeSLotDto;
import org.example.reservationservice.dto.OutputTimeSlotDto;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotMapper {

    public TimeSlot toDomain(InputTimeSLotDto dto, Restaurant restaurant) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setRestaurant(restaurant);
        timeSlot.setStartTime(dto.getStartTime());
        timeSlot.setEndTime(dto.getEndTime());
        timeSlot.setSeatCount(dto.getSeatCount());
        timeSlot.setSmokingAllowed(dto.getSmokingAllowed());
        timeSlot.setOutdoorSeating(dto.getOutdoorSeating());
        timeSlot.setVisible(true);
        return timeSlot;
    }

    public void updateDomain(TimeSlot timeSlot, InputTimeSLotDto dto) {
        if (dto.getStartTime() != null) timeSlot.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) timeSlot.setEndTime(dto.getEndTime());
        if (dto.getSeatCount() != null) timeSlot.setSeatCount(dto.getSeatCount());
        if (dto.getSmokingAllowed() != null) timeSlot.setSmokingAllowed(dto.getSmokingAllowed());
        if (dto.getOutdoorSeating() != null) timeSlot.setOutdoorSeating(dto.getOutdoorSeating());
    }

    public OutputTimeSlotDto toDto(TimeSlot timeSlot) {
        OutputTimeSlotDto dto = new OutputTimeSlotDto();
        dto.setId(timeSlot.getId());
        dto.setRestaurantId(timeSlot.getRestaurant().getId());
        dto.setRestaurantName(timeSlot.getRestaurant().getName());
        dto.setStartTime(timeSlot.getStartTime());
        dto.setEndTime(timeSlot.getEndTime());
        dto.setSeatCount(timeSlot.getSeatCount());
        dto.setSmokingAllowed(timeSlot.getSmokingAllowed());
        dto.setOutdoorSeating(timeSlot.getOutdoorSeating());
        dto.setVisible(timeSlot.getVisible());
        return dto;
    }
}
package org.example.reservationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.reservationservice.domain.CuisineType;

@Data
@AllArgsConstructor
public class CuisineTypeDto {
    private CuisineType type;
    private String displayName;
}

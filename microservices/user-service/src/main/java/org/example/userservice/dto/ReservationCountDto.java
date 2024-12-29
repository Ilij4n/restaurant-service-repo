package org.example.userservice.dto;

import lombok.Data;

// Sluzi reservation serviceu da gettuje broj rezervacija
@Data
public class ReservationCountDto {
    private Long userId;
    private Integer count;
}

package org.example.reservationservice.dto;

import lombok.Data;

// General purpose - verovatno ce zatrebati ako nemamo nikakve konkretne podatke da prosledimo u odgovoru
@Data // kraci zapis za gettere, settere i noargsConst
public class MessageResponseDto {
    private String message;
    private Boolean success;  // Optional, if we want to include it besides HTTP status
}

package org.example.userservice.dto;

import lombok.Data;
import lombok.Builder;

@Data
// @Builder // - Zanimljiva anotacija koja implementira Builder pattern nad nasom klasom, imam samo dva polja, pa cu se ipak sluziti setterima
public class LoginResponseDto {
    private String token; // U claimsu moze sadrzati Id-usera i njegovu rolu, decodeovace i raspakovati na frontu
    private String tokenType; // u nasem slucaju = Bearer
}
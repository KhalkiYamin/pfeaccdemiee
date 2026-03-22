package com.pfe.pfeaccdemie.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteSeanceDto {

    private Long id;
    private String theme;
    private LocalDate dateSeance;
    private LocalTime heureSeance;
    private String lieu;
    private String coachNomComplet;
    private String specialite;
}
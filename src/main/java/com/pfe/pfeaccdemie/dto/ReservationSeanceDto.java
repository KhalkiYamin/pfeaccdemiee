package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSeanceDto {

    private Long id;

    private Long seanceId;
    private String theme;
    private String dateSeance;
    private String heureSeance;
    private String lieu;

    private Long athleteId;
    private String athleteNomComplet;
    private String athleteEmail;

    private String statut;
    private String dateReservation;
}
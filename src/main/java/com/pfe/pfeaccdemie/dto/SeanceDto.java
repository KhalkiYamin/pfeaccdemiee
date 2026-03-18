package com.pfe.pfeaccdemie.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeanceDto {

    private Long id;
    private String theme;
    private String description;
    private String dateSeance;
    private String heureSeance;
    private String groupe;
    private String lieu;
    private Integer nombreAthletes;
    private String statut;
    private String duree;
    private String objectif;
    private Long coachId;
    private String coachNom;
}
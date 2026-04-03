package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeanceDto {

    private Long id;
    private String theme;
    private String description;
    private String dateSeance;
    private String heureSeance;
    private String lieu;
    private Integer nombreAthletes;
    private String statut;
    private String duree;
    private String objectif;

    private Long coachId;
    private String coachNom;

    private Long sportId;
    private String sportTitle;
    private String niveau;

    private String groupe;

    private List<Long> ressourceIds;

    // ✅ fields jded
    private Boolean annuleeMoinsDe24h;
    private String messageAnnulation;
}
package com.pfe.pfeaccdemie.dto;

import lombok.Data;

@Data
public class CoachExperienceDto {
    private Long id;
    private String nomClub;
    private String dateDebut;
    private String dateFin;
    private String poste;
}
package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteCoachDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String imageProfil;
    private String specialite;
    private Integer experience;
}
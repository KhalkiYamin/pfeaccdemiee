package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCoachProfileDto {

    private String prenom;
    private String nom;
    private String email;
    private String telephone;
    private Integer experience;
}
package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthleteProfileResponse {
    private String nom;
    private String prenom;
    private String email;
    private String sport;
    private String niveau;
    private String telephone;
}
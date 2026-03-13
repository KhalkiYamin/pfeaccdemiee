package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private String specialite;
    private String sport;
    private String niveau;
    private String statut;
    private Boolean enabled;
}
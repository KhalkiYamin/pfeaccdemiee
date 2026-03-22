package com.pfe.pfeaccdemie.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CoachProfileRequest {

    private String nom;
    private String prenom;
    private String telephone;
    private String imageProfil;

    private String nomUtilisateur;
    private String genre;
    private LocalDate dateNaissance;
    private String biographie;

    private String nomClub;
    private String adresseClub;
    private String clubImage;

    private String adresseLigne1;
    private String adresseLigne2;
    private String ville;
    private String etatProvince;
    private String pays;
    private String codePostal;

    private List<String> services;
    private List<String> specialisations;

    private List<CoachDiplomaDto> diplomes;
    private List<CoachExperienceDto> experiences;
    private List<CoachRewardDto> recompenses;
}
package com.pfe.pfeaccdemie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Le nom est requis")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    private String telephone;

    @NotBlank(message = "Le rôle est requis")
    private String role; // "COACH" ou "ATHLETE"

    private boolean enabled = true;

    // Champs spécifiques pour Coach
    private String specialite;
    private Integer experience;

    // Champs spécifiques pour Athlète
    private String sport;
    private String niveau; // DEBUTANT, INTERMEDIAIRE, CONFIRME, PROFESSIONNEL
}
package com.pfe.pfeaccdemie.dto;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;

    private Long sportId;
    private String niveau;

    private Long specialiteId;
    private Integer experience;
}
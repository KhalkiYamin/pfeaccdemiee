package com.pfe.pfeaccdemie.dto;




import lombok.Data;

@Data
public class RessourceSportifDto {
    private Long id;
    private String nom;
    private String description;
    private Boolean disponibilite;
    private String image;
}
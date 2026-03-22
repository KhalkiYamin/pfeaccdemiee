package com.pfe.pfeaccdemie.dto;

import lombok.Data;

@Data
public class CoachDiplomaDto {
    private Long id;
    private String diplome;
    private String ecoleInstitut;
    private String anneeObtention;
}
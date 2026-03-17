package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachAthleteDto {
    private Long id;
    private String nomComplet;
    private String sport;
    private String niveau;
    private String statutPresence;
}
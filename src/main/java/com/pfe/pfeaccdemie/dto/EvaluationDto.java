package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDto {
    private Long athleteId;
    private String athlete;
    private Integer technique;
    private Integer physique;
    private Integer mental;
}
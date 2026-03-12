package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDto {
    private Long totalCoachs;
    private Long totalAthletes;
    private Double totalPayments;
    private Long totalResources;
    private Integer activityRate;
    private Long activeSubscriptions;
    private Long plannedSessions;
    private Integer globalSatisfaction;
}
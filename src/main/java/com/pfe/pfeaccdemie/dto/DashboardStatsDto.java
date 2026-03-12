package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsDto {

    private long totalUsers;
    private long totalAthletes;
    private long totalCoaches;
    private long pendingCoaches;
    private long totalResources;
    private double totalPayments;
    private int activityRate;
    private long activeSubscriptions;
    private long plannedSessions;
    private int globalSatisfaction;
}
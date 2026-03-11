package com.pfe.pfeaccdemie.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminStats {

    private long totalUsers;
    private long totalAthletes;
    private long totalCoaches;
    private long pendingCoaches;

}
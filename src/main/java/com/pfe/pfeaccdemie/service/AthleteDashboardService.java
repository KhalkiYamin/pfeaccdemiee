package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;

import java.util.List;

public interface AthleteDashboardService {
    List<AthleteSeanceDto> getAthleteSeances(String email);
}
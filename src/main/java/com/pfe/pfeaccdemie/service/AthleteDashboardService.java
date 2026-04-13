package com.pfe.pfeaccdemie.service;

import java.util.List;

import com.pfe.pfeaccdemie.dto.AthleteCoachDto;
import com.pfe.pfeaccdemie.dto.AthletePresenceSummaryResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileUpdateRequest;
import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;

public interface AthleteDashboardService {
    List<AthleteSeanceDto> getAthleteSeances(String email);
    List<AthleteCoachDto> getAthleteCoaches(String email);
    AthletePresenceSummaryResponse getAthletePresenceSummary(String email);
    AthleteProfileResponse getAthleteProfile(String email);
    AthleteProfileResponse updateAthleteProfile(String email, AthleteProfileUpdateRequest request);
}
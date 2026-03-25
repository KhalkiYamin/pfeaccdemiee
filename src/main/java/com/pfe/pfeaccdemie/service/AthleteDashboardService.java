package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;
import com.pfe.pfeaccdemie.dto.AthletePresenceSummaryResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileUpdateRequest;
import java.util.List;

public interface AthleteDashboardService {
    List<AthleteSeanceDto> getAthleteSeances(String email);
    AthletePresenceSummaryResponse getAthletePresenceSummary(String email);
    AthleteProfileResponse getAthleteProfile(String email);
    AthleteProfileResponse updateAthleteProfile(String email, AthleteProfileUpdateRequest request);
}
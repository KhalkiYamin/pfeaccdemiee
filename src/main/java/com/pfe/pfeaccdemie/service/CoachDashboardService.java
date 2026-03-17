package com.pfe.pfeaccdemie.service;

import java.util.List;

import com.pfe.pfeaccdemie.dto.CoachAthleteDto;
import com.pfe.pfeaccdemie.dto.CoachProfileDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.pfe.pfeaccdemie.dto.UpdateCoachProfileDto;
import com.pfe.pfeaccdemie.dto.EvaluationDto;
import com.pfe.pfeaccdemie.dto.PresenceDto;
public interface CoachDashboardService {
    CoachProfileDto getCoachProfile(String email);
    List<CoachAthleteDto> getMyAthletes(String email);
    CoachProfileDto uploadCoachPhoto(String email, MultipartFile image) throws IOException;
    CoachProfileDto updateCoachProfile(String currentEmail, UpdateCoachProfileDto dto);
    List<EvaluationDto> getMyEvaluations(String email);
    List<PresenceDto> getMyPresences(String email);

}
package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.SeanceDto;

import java.time.LocalDate;
import java.util.List;

public interface SeanceService {

    SeanceDto createSeance(SeanceDto dto);

    List<SeanceDto> getAllSeances();

    List<SeanceDto> getSeancesByCoach(Long coachId);

    List<SeanceDto> filterSeances(Long coachId, String statut, String niveau, LocalDate dateSeance);

    SeanceDto getSeanceById(Long id);

    SeanceDto updateSeance(Long id, SeanceDto dto);

    String assignAthleteToSeance(Long seanceId, Long athleteId);

    void deleteSeance(Long seanceId);
}
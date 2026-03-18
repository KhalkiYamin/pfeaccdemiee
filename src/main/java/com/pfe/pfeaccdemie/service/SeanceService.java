package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.SeanceDto;

import java.time.LocalDate;
import java.util.List;

public interface SeanceService {

    SeanceDto createSeance(SeanceDto dto);

    SeanceDto updateSeance(Long id, SeanceDto dto);

    void deleteSeance(Long id);

    SeanceDto getSeanceById(Long id);

    List<SeanceDto> getAllSeances();

    List<SeanceDto> getSeancesByCoach(Long coachId);

    List<SeanceDto> filterSeances(Long coachId, String statut, String groupe, LocalDate dateSeance);
}
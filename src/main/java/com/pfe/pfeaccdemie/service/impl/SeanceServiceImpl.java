package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements SeanceService {

    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;

    @Override
    public SeanceDto createSeance(SeanceDto dto) {
        User coach = userRepository.findById(dto.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        Seance seance = Seance.builder()
                .theme(dto.getTheme())
                .description(dto.getDescription())
                .dateSeance(LocalDate.parse(dto.getDateSeance()))
                .heureSeance(LocalTime.parse(dto.getHeureSeance()))
                .groupe(dto.getGroupe())
                .lieu(dto.getLieu())
                .nombreAthletes(dto.getNombreAthletes())
                .statut(dto.getStatut())
                .duree(dto.getDuree())
                .objectif(dto.getObjectif())
                .coach(coach)
                .build();

        return mapToDto(seanceRepository.save(seance));
    }

    @Override
    public SeanceDto updateSeance(Long id, SeanceDto dto) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if (dto.getCoachId() != null) {
            User coach = userRepository.findById(dto.getCoachId())
                    .orElseThrow(() -> new RuntimeException("Coach introuvable"));
            seance.setCoach(coach);
        }

        seance.setTheme(dto.getTheme());
        seance.setDescription(dto.getDescription());
        seance.setDateSeance(LocalDate.parse(dto.getDateSeance()));
        seance.setHeureSeance(LocalTime.parse(dto.getHeureSeance()));
        seance.setGroupe(dto.getGroupe());
        seance.setLieu(dto.getLieu());
        seance.setNombreAthletes(dto.getNombreAthletes());
        seance.setStatut(dto.getStatut());
        seance.setDuree(dto.getDuree());
        seance.setObjectif(dto.getObjectif());

        return mapToDto(seanceRepository.save(seance));
    }

    @Override
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    @Override
    public SeanceDto getSeanceById(Long id) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));
        return mapToDto(seance);
    }

    @Override
    public List<SeanceDto> getAllSeances() {
        return seanceRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceDto> getSeancesByCoach(Long coachId) {
        return seanceRepository.findByCoachId(coachId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceDto> filterSeances(Long coachId, String statut, String groupe, LocalDate dateSeance) {
        List<Seance> seances = seanceRepository.findByCoachId(coachId);

        if (statut != null && !statut.isBlank()) {
            seances = seances.stream()
                    .filter(s -> statut.equalsIgnoreCase(s.getStatut()))
                    .collect(Collectors.toList());
        }

        if (groupe != null && !groupe.isBlank()) {
            seances = seances.stream()
                    .filter(s -> groupe.equalsIgnoreCase(s.getGroupe()))
                    .collect(Collectors.toList());
        }

        if (dateSeance != null) {
            seances = seances.stream()
                    .filter(s -> dateSeance.equals(s.getDateSeance()))
                    .collect(Collectors.toList());
        }

        return seances.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private SeanceDto mapToDto(Seance seance) {
        return SeanceDto.builder()
                .id(seance.getId())
                .theme(seance.getTheme())
                .description(seance.getDescription())
                .dateSeance(seance.getDateSeance() != null ? seance.getDateSeance().toString() : null)
                .heureSeance(seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : null)
                .groupe(seance.getGroupe())
                .lieu(seance.getLieu())
                .nombreAthletes(seance.getNombreAthletes())
                .statut(seance.getStatut())
                .duree(seance.getDuree())
                .objectif(seance.getObjectif())
                .coachId(seance.getCoach() != null ? seance.getCoach().getId() : null)
                .coachNom(seance.getCoach() != null ? seance.getCoach().getNom() + " " + seance.getCoach().getPrenom() : null)
                .build();
    }
}
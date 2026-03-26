package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.entities.Category;
import com.pfe.pfeaccdemie.entities.RessourceSportif;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.CategoryRepository;
import com.pfe.pfeaccdemie.repositories.PresenceRepository;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.RessourceSportifRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements SeanceService {

    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PresenceRepository presenceRepository;
    private final ReservationSeanceRepository reservationSeanceRepository;
    private final RessourceSportifRepository ressourceSportifRepository;

    @Override
    public SeanceDto createSeance(SeanceDto dto) {
        User coach = userRepository.findById(dto.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (dto.getSportId() == null) {
            throw new RuntimeException("Le sport est obligatoire");
        }

        Category sport = categoryRepository.findById(dto.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport introuvable"));

        List<RessourceSportif> ressources = dto.getRessourceIds() == null || dto.getRessourceIds().isEmpty()
                ? new ArrayList<>()
                : ressourceSportifRepository.findAllById(dto.getRessourceIds());

        Seance seance = Seance.builder()
                .theme(dto.getTheme())
                .description(dto.getDescription())
                .dateSeance(parseDate(dto.getDateSeance()))
                .heureSeance(parseTime(dto.getHeureSeance()))
                .lieu(dto.getLieu())
                .statut(dto.getStatut())
                .duree(dto.getDuree())
                .objectif(dto.getObjectif())
                .coach(coach)
                .sport(sport)
                .niveau(dto.getNiveau())
                .ressources(ressources)
                .build();

        Seance saved = seanceRepository.save(seance);
        return mapToDto(saved);
    }

    @Override
    public List<SeanceDto> getAllSeances() {
        return seanceRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<SeanceDto> getSeancesByCoach(Long coachId) {
        return seanceRepository.findByCoachId(coachId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<SeanceDto> filterSeances(Long coachId, String statut, String niveau, LocalDate dateSeance) {
        List<Seance> seances;

        if (statut != null && !statut.isBlank()) {
            seances = seanceRepository.findByCoachIdAndStatut(coachId, statut);
        } else if (dateSeance != null) {
            seances = seanceRepository.findByCoachIdAndDateSeance(coachId, dateSeance);
        } else {
            seances = seanceRepository.findByCoachId(coachId);
        }

        if (niveau != null && !niveau.isBlank()) {
            seances = seances.stream()
                    .filter(s -> s.getNiveau() != null && s.getNiveau().equalsIgnoreCase(niveau))
                    .toList();
        }

        if (dateSeance != null && statut != null && !statut.isBlank()) {
            seances = seances.stream()
                    .filter(s -> s.getDateSeance() != null && s.getDateSeance().equals(dateSeance))
                    .toList();
        }

        return seances.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public SeanceDto getSeanceById(Long id) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));
        return mapToDto(seance);
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

        if (dto.getSportId() != null) {
            Category sport = categoryRepository.findById(dto.getSportId())
                    .orElseThrow(() -> new RuntimeException("Sport introuvable"));
            seance.setSport(sport);
        }

        if (dto.getTheme() != null) {
            seance.setTheme(dto.getTheme());
        }

        if (dto.getDescription() != null) {
            seance.setDescription(dto.getDescription());
        }

        if (dto.getDateSeance() != null && !dto.getDateSeance().isBlank()) {
            seance.setDateSeance(parseDate(dto.getDateSeance()));
        }

        if (dto.getHeureSeance() != null && !dto.getHeureSeance().isBlank()) {
            seance.setHeureSeance(parseTime(dto.getHeureSeance()));
        }

        if (dto.getLieu() != null) {
            seance.setLieu(dto.getLieu());
        }

        if (dto.getStatut() != null) {
            seance.setStatut(dto.getStatut());
        }

        if (dto.getDuree() != null) {
            seance.setDuree(dto.getDuree());
        }

        if (dto.getObjectif() != null) {
            seance.setObjectif(dto.getObjectif());
        }

        if (dto.getNiveau() != null) {
            seance.setNiveau(dto.getNiveau());
        }

        if (dto.getRessourceIds() != null) {
            List<RessourceSportif> ressources = dto.getRessourceIds().isEmpty()
                    ? new ArrayList<>()
                    : ressourceSportifRepository.findAllById(dto.getRessourceIds());
            seance.setRessources(ressources);
        }

        Seance updated = seanceRepository.save(seance);
        return mapToDto(updated);
    }

    @Override
    public String assignAthleteToSeance(Long seanceId, Long athleteId) {
        throw new RuntimeException("Cette méthode n'est plus utilisée. Utilisez le système de réservation.");
    }

    @Override
    @Transactional
    public void deleteSeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        presenceRepository.deleteBySeanceId(seanceId);
        reservationSeanceRepository.deleteBySeanceId(seanceId);
        seanceRepository.delete(seance);
    }

    private SeanceDto mapToDto(Seance seance) {
        String coachNom = "";
        if (seance.getCoach() != null) {
            String prenom = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nom = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachNom = (prenom + " " + nom).trim();
        }

        long nombreAthletes = reservationSeanceRepository.countBySeanceIdAndStatut(
                seance.getId(),
                StatutReservation.ACCEPTEE
        );

        String groupe = null;
        if (seance.getSport() != null && seance.getNiveau() != null && !seance.getNiveau().isBlank()) {
            groupe = seance.getSport().getTitle() + " - " + seance.getNiveau();
        }

        List<Long> ressourceIds = seance.getRessources() == null
                ? List.of()
                : seance.getRessources().stream()
                .map(RessourceSportif::getId)
                .toList();

        return SeanceDto.builder()
                .id(seance.getId())
                .theme(seance.getTheme())
                .description(seance.getDescription())
                .dateSeance(seance.getDateSeance() != null ? seance.getDateSeance().toString() : null)
                .heureSeance(seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : null)
                .lieu(seance.getLieu())
                .nombreAthletes((int) nombreAthletes)
                .statut(seance.getStatut())
                .duree(seance.getDuree())
                .objectif(seance.getObjectif())
                .coachId(seance.getCoach() != null ? seance.getCoach().getId() : null)
                .coachNom(coachNom)
                .sportId(seance.getSport() != null ? seance.getSport().getId() : null)
                .sportTitle(seance.getSport() != null ? seance.getSport().getTitle() : null)
                .niveau(seance.getNiveau())
                .groupe(groupe)
                .ressourceIds(ressourceIds)
                .build();
    }

    private LocalDate parseDate(String date) {
        return (date == null || date.isBlank()) ? null : LocalDate.parse(date);
    }

    private LocalTime parseTime(String time) {
        return (time == null || time.isBlank()) ? null : LocalTime.parse(time);
    }
}
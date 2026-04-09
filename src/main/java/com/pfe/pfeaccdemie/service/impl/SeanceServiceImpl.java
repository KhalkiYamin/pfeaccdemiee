package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.entities.Category;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
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
import com.pfe.pfeaccdemie.service.EmailService;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final EmailService emailService;

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

        if (dto.getStatut() != null && isStatusAnnulee(dto.getStatut())) {
            gererAnnulationReservations(updated);
        }

        return mapToDto(updated);
    }

    @Override
    public String assignAthleteToSeance(Long seanceId, Long athleteId) {
        throw new RuntimeException("Cette méthode n'est plus utilisée. Utilisez le système de réservation.");
    }

    @Override
    public SeanceDto annulerSeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if (isStatusAnnulee(seance.getStatut())) {
            throw new RuntimeException("Cette séance est déjà annulée");
        }

        seance.setStatut("ANNULEE");
        Seance saved = seanceRepository.save(seance);

        gererAnnulationReservations(saved);

        return mapToDto(saved);
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

    private void gererAnnulationReservations(Seance seance) {
        if (seance.getDateSeance() == null || seance.getHeureSeance() == null) {
            return;
        }

        LocalDateTime dateHeureSeance = LocalDateTime.of(
                seance.getDateSeance(),
                seance.getHeureSeance()
        );

        boolean moinsDe24h = LocalDateTime.now().isAfter(dateHeureSeance.minusHours(24));

        List<ReservationSeance> reservations = reservationSeanceRepository.findBySeanceId(seance.getId());

        for (ReservationSeance r : reservations) {
            if (r.getStatut() == StatutReservation.EN_ATTENTE || r.getStatut() == StatutReservation.ACCEPTEE) {
                r.setStatut(StatutReservation.REFUSEE);
                reservationSeanceRepository.save(r);
            }

            if (r.getAthlete() != null && r.getAthlete().getEmail() != null && !r.getAthlete().getEmail().isBlank()) {
                String athleteFullName = ((r.getAthlete().getPrenom() != null ? r.getAthlete().getPrenom() : "") + " " +
                        (r.getAthlete().getNom() != null ? r.getAthlete().getNom() : "")).trim();

                String msg = moinsDe24h
                        ? "⚠️ Cette séance a été annulée moins de 24h avant. Votre réservation a été refusée automatiquement."
                        : "Cette séance a été annulée. Votre réservation a été refusée automatiquement.";

                String content = """
                    <div style='font-family: Arial, sans-serif; padding: 24px; color: #1f2937; background: #f9fafb;'>
                        <div style='max-width: 620px; margin: auto; background: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb;'>
                            <div style='background: linear-gradient(135deg, #dc2626, #ef4444); padding: 24px; color: white;'>
                                <h1 style='margin: 0; font-size: 24px;'>Académie Sportive</h1>
                                <p style='margin: 8px 0 0; font-size: 14px; opacity: 0.95;'>Séance annulée</p>
                            </div>

                            <div style='padding: 28px;'>
                                <h2 style='margin-top: 0; color: #111827;'>Bonjour %s,</h2>

                                <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                                    %s
                                </p>

                                <div style='background: #fef2f2; border-radius: 12px; padding: 18px; margin: 22px 0; border: 1px solid #fecaca;'>
                                    <p style='margin: 8px 0;'><strong>Thème :</strong> %s</p>
                                    <p style='margin: 8px 0;'><strong>Date :</strong> %s</p>
                                    <p style='margin: 8px 0;'><strong>Heure :</strong> %s</p>
                                    <p style='margin: 8px 0;'><strong>Lieu :</strong> %s</p>
                                </div>

                                <p style='margin-top: 28px; color: #6b7280; font-size: 14px;'>
                                    Cordialement,<br>
                                    <strong>L'équipe Académie Sportive</strong>
                                </p>
                            </div>
                        </div>
                    </div>
                    """.formatted(
                        athleteFullName.isBlank() ? "Athlète" : athleteFullName,
                        msg,
                        seance.getTheme() != null ? seance.getTheme() : "-",
                        seance.getDateSeance() != null ? seance.getDateSeance().toString() : "-",
                        seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : "-",
                        seance.getLieu() != null ? seance.getLieu() : "-"
                );

                emailService.sendEmail(r.getAthlete().getEmail(), "Séance annulée", content);
            }
        }
    }

    private boolean isStatusAnnulee(String statut) {
        if (statut == null) {
            return false;
        }

        String normalized = Normalizer.normalize(statut, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .trim();

        return normalized.contains("ANNUL");
    }

    private SeanceDto mapToDto(Seance seance) {
        String coachNom = "";
        if (seance.getCoach() != null) {
            String prenom = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nom = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachNom = (prenom + " " + nom).trim();
        }

        long nombreAthletes = 0;
        try {
            nombreAthletes = reservationSeanceRepository.countBySeanceIdAndStatut(
                    seance.getId(),
                    StatutReservation.ACCEPTEE
            );
        } catch (Exception e) {
            System.out.println("Erreur count reservations: " + e.getMessage());
            nombreAthletes = 0;
        }

        String groupe = null;
        if (seance.getSport() != null
                && seance.getSport().getTitle() != null
                && seance.getNiveau() != null
                && !seance.getNiveau().isBlank()) {
            groupe = seance.getSport().getTitle() + " - " + seance.getNiveau();
        }

        List<Long> ressourceIds = seance.getRessources() == null
                ? List.of()
                : seance.getRessources().stream()
                .map(RessourceSportif::getId)
                .toList();

        Boolean annuleeMoinsDe24h = false;
        String messageAnnulation = null;

        if (isStatusAnnulee(seance.getStatut())
                && seance.getDateSeance() != null
                && seance.getHeureSeance() != null) {

            LocalDateTime dateHeureSeance = LocalDateTime.of(
                    seance.getDateSeance(),
                    seance.getHeureSeance()
            );

            annuleeMoinsDe24h = LocalDateTime.now().isAfter(dateHeureSeance.minusHours(24));

            messageAnnulation = annuleeMoinsDe24h
                    ? "Annulée moins de 24h"
                    : "Cette séance a été annulée";
        }

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
                .annuleeMoinsDe24h(annuleeMoinsDe24h)
                .messageAnnulation(messageAnnulation)
                .build();
    }

    private LocalDate parseDate(String date) {
        return (date == null || date.isBlank()) ? null : LocalDate.parse(date);
    }

    private LocalTime parseTime(String time) {
        return (time == null || time.isBlank()) ? null : LocalTime.parse(time);
    }
    @Override
    public SeanceDto getLastSessionForAthlete(String email) {

        ReservationSeance reservation =
                reservationSeanceRepository.findTopByAthleteEmailOrderByDateReservationDesc(email);

        if (reservation == null || reservation.getSeance() == null) {
            return null;
        }

        Seance seance = reservation.getSeance();

        return mapToDto(seance);
    }
}
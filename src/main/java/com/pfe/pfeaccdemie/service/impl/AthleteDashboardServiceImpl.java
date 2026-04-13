package com.pfe.pfeaccdemie.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.pfe.pfeaccdemie.dto.AthleteCoachDto;
import com.pfe.pfeaccdemie.dto.AthletePresenceSummaryResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileResponse;
import com.pfe.pfeaccdemie.dto.AthleteProfileUpdateRequest;
import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;
import com.pfe.pfeaccdemie.entities.Presence;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.StatutPresence;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.PresenceRepository;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.AthleteDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AthleteDashboardServiceImpl implements AthleteDashboardService {

    private final PresenceRepository presenceRepository;
    private final UserRepository userRepository;
    private final ReservationSeanceRepository reservationSeanceRepository;

    @Override
    public List<AthleteSeanceDto> getAthleteSeances(String email) {
        List<ReservationSeance> reservations = reservationSeanceRepository.findByAthleteEmail(email);
        List<AthleteSeanceDto> result = new ArrayList<>();

        for (ReservationSeance reservation : reservations) {
            if (reservation.getStatut() != StatutReservation.ACCEPTEE) {
                continue;
            }

            Seance seance = reservation.getSeance();
            if (seance == null) {
                continue;
            }

            String coachNomComplet = "";
            String specialite = "Spécialité non définie";

            if (seance.getCoach() != null) {
                String prenom = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
                String nom = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
                coachNomComplet = (prenom + " " + nom).trim();

                if (seance.getCoach().getSpecialite() != null) {
                    specialite = seance.getCoach().getSpecialite().getTitle();
                }
            }

            AthleteSeanceDto dto = AthleteSeanceDto.builder()
                    .id(seance.getId())
                    .theme(seance.getTheme())
                    .dateSeance(seance.getDateSeance())
                    .heureSeance(seance.getHeureSeance())
                    .lieu(seance.getLieu())
                    .coachNomComplet(coachNomComplet)
                    .specialite(specialite)
                    .build();

            result.add(dto);
        }

        return result;
    }

    @Override
    public List<AthleteCoachDto> getAthleteCoaches(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        List<User> coaches;
        if (athlete.getSport() != null) {
            coaches = userRepository.findByRoleAndSport_IdAndEnabledAndAdminApproved(
                    Role.COACH,
                    athlete.getSport().getId(),
                    true,
                    true
            );
        } else {
            coaches = userRepository.findByRoleAndEnabledAndAdminApproved(Role.COACH, true, true);
        }

        return coaches.stream()
                .map(coach -> AthleteCoachDto.builder()
                        .id(coach.getId())
                        .nom(coach.getNom())
                        .prenom(coach.getPrenom())
                        .email(coach.getEmail())
                        .telephone(coach.getTelephone())
                        .imageProfil(coach.getImageProfil())
                        .specialite(coach.getSpecialite() != null ? coach.getSpecialite().getTitle() : "")
                        .experience(coach.getExperience() != null ? coach.getExperience() : 0)
                        .build())
                .toList();
    }

    @Override
    public AthletePresenceSummaryResponse getAthletePresenceSummary(String email) {
        List<Presence> presences = presenceRepository.findByAthleteEmail(email);

        long total = presences.size();

        long present = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.PRESENT)
                .count();

        long absent = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.ABSENT)
                .count();

        long retard = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.RETARD)
                .count();

        int rate = total == 0 ? 0 : (int) Math.round(((double) (present + retard) / total) * 100);

        String label;
        if (rate >= 90) {
            label = "Excellent";
        } else if (rate >= 75) {
            label = "Bon";
        } else if (rate >= 50) {
            label = "Moyen";
        } else {
            label = "Faible";
        }

        return AthletePresenceSummaryResponse.builder()
                .presenceRate(rate)
                .presenceLabel(label)
                .presentCount(present)
                .absentCount(absent)
                .retardCount(retard)
                .totalSeances(total)
                .build();
    }

    @Override
    public AthleteProfileResponse getAthleteProfile(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        String sport = athlete.getSport() != null ? athlete.getSport().getTitle() : "";
        String niveau = athlete.getNiveau() != null ? athlete.getNiveau() : "";
        String telephone = athlete.getTelephone() != null ? athlete.getTelephone() : "";

        return AthleteProfileResponse.builder()
                .nom(athlete.getNom() != null ? athlete.getNom() : "")
                .prenom(athlete.getPrenom() != null ? athlete.getPrenom() : "")
                .email(athlete.getEmail() != null ? athlete.getEmail() : "")
                .sport(sport)
                .niveau(niveau)
                .telephone(telephone)
                .build();
    }

    @Override
    public AthleteProfileResponse updateAthleteProfile(String email, AthleteProfileUpdateRequest request) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (request.getNom() != null) {
            athlete.setNom(request.getNom().trim());
        }

        if (request.getPrenom() != null) {
            athlete.setPrenom(request.getPrenom().trim());
        }

        if (request.getTelephone() != null) {
            athlete.setTelephone(request.getTelephone().trim());
        }

        if (request.getNiveau() != null) {
            athlete.setNiveau(request.getNiveau().trim());
        }

        User saved = userRepository.save(athlete);

        String sport = saved.getSport() != null ? saved.getSport().getTitle() : "";
        String niveau = saved.getNiveau() != null ? saved.getNiveau() : "";
        String telephone = saved.getTelephone() != null ? saved.getTelephone() : "";

        return AthleteProfileResponse.builder()
                .nom(saved.getNom() != null ? saved.getNom() : "")
                .prenom(saved.getPrenom() != null ? saved.getPrenom() : "")
                .email(saved.getEmail() != null ? saved.getEmail() : "")
                .sport(sport)
                .niveau(niveau)
                .telephone(telephone)
                .build();
    }
}
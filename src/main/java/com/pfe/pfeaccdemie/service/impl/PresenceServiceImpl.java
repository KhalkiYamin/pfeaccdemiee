package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.PresenceRequest;
import com.pfe.pfeaccdemie.dto.PresenceResponse;
import com.pfe.pfeaccdemie.entities.Presence;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.StatutPresence;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.PresenceRepository;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final PresenceRepository presenceRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final ReservationSeanceRepository reservationSeanceRepository;

    @Override
    public List<PresenceResponse> getPresencesBySeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        List<User> athletes = reservationSeanceRepository
                .findBySeanceIdAndStatut(seance.getId(), StatutReservation.ACCEPTEE)
                .stream()
                .map(ReservationSeance::getAthlete)
                .toList();

        List<PresenceResponse> responses = new ArrayList<>();

        for (User athlete : athletes) {
            Presence presence = presenceRepository
                    .findBySeanceIdAndAthleteId(seanceId, athlete.getId())
                    .orElse(null);

            String statut = (presence != null)
                    ? presence.getStatut().name()
                    : StatutPresence.EN_ATTENTE.name();

            String nom = athlete.getNom() != null ? athlete.getNom() : "";
            String prenom = athlete.getPrenom() != null ? athlete.getPrenom() : "";

            responses.add(PresenceResponse.builder()
                    .athleteId(athlete.getId())
                    .nomComplet((nom + " " + prenom).trim())
                    .statut(statut)
                    .build());
        }

        return responses;
    }

    @Override
    public PresenceResponse updatePresence(Long seanceId, Long athleteId, PresenceRequest request) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        User athlete = userRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        ReservationSeance reservation = reservationSeanceRepository
                .findBySeanceIdAndAthleteId(seanceId, athleteId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable pour cet athlète"));

        if (reservation.getStatut() != StatutReservation.ACCEPTEE) {
            throw new RuntimeException("La présence ne peut être modifiée que pour une réservation acceptée");
        }

        Presence presence = presenceRepository
                .findBySeanceIdAndAthleteId(seanceId, athleteId)
                .orElse(
                        Presence.builder()
                                .seance(seance)
                                .athlete(athlete)
                                .statut(StatutPresence.EN_ATTENTE)
                                .build()
                );

        presence.setStatut(StatutPresence.valueOf(request.getStatut().toUpperCase()));

        Presence saved = presenceRepository.save(presence);

        String nom = saved.getAthlete().getNom() != null ? saved.getAthlete().getNom() : "";
        String prenom = saved.getAthlete().getPrenom() != null ? saved.getAthlete().getPrenom() : "";

        return PresenceResponse.builder()
                .athleteId(saved.getAthlete().getId())
                .nomComplet((nom + " " + prenom).trim())
                .statut(saved.getStatut().name())
                .build();
    }
}
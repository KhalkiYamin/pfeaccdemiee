package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.ReservationSeanceDto;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.ReservationSeanceRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.EmailService;
import com.pfe.pfeaccdemie.service.ReservationSeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationSeanceServiceImpl implements ReservationSeanceService {

    private final ReservationSeanceRepository reservationSeanceRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public List<ReservationSeanceDto> getSeancesDisponiblesPourAthlete(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (athlete.getRole() != Role.ATHLETE) {
            throw new RuntimeException("Seul un athlète peut consulter les séances disponibles pour réservation");
        }

        if (athlete.getSport() == null) {
            throw new RuntimeException("Aucun sport associé à cet athlète");
        }

        List<Seance> seances = seanceRepository.findBySportId(athlete.getSport().getId());

        return seances.stream()
                .map(seance -> {
                    ReservationSeance existing = reservationSeanceRepository
                            .findBySeanceIdAndAthleteId(seance.getId(), athlete.getId())
                            .orElse(null);

                    return mapToDtoForAthleteView(seance, athlete, existing);
                })
                .toList();
    }

    @Override
    public ReservationSeanceDto reserverSeance(Long seanceId, String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (athlete.getRole() != Role.ATHLETE) {
            throw new RuntimeException("Seul un athlète peut réserver une séance");
        }

        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if (athlete.getSport() == null || seance.getSport() == null) {
            throw new RuntimeException("Sport non défini pour l'athlète ou la séance");
        }

        if (!athlete.getSport().getId().equals(seance.getSport().getId())) {
            throw new RuntimeException("Cette séance ne correspond pas au sport de l'athlète");
        }

        if (reservationSeanceRepository.findBySeanceIdAndAthleteId(seanceId, athlete.getId()).isPresent()) {
            throw new RuntimeException("Vous avez déjà une réservation pour cette séance");
        }

        ReservationSeance reservation = ReservationSeance.builder()
                .seance(seance)
                .athlete(athlete)
                .statut(StatutReservation.EN_ATTENTE)
                .dateReservation(LocalDateTime.now())
                .build();

        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        return mapToDto(saved);
    }

    @Override
    public List<ReservationSeanceDto> getReservationsBySeance(Long seanceId) {
        seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        return reservationSeanceRepository.findBySeanceId(seanceId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ReservationSeanceDto accepterReservation(Long reservationId) {
        ReservationSeance reservation = reservationSeanceRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        reservation.setStatut(StatutReservation.ACCEPTEE);
        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        sendSeanceEmail(saved.getSeance(), saved.getAthlete());

        return mapToDto(saved);
    }

    @Override
    public ReservationSeanceDto refuserReservation(Long reservationId) {
        ReservationSeance reservation = reservationSeanceRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        reservation.setStatut(StatutReservation.REFUSEE);
        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        sendReservationRefusedEmail(saved.getSeance(), saved.getAthlete());

        return mapToDto(saved);
    }

    @Override
    public List<ReservationSeanceDto> getMesReservations(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (athlete.getRole() != Role.ATHLETE) {
            throw new RuntimeException("Seul un athlète peut consulter ses réservations");
        }

        return reservationSeanceRepository.findByAthleteEmail(email)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    private ReservationSeanceDto mapToDto(ReservationSeance reservation) {
        Seance seance = reservation.getSeance();
        User athlete = reservation.getAthlete();

        String athleteNom = "";
        if (athlete != null) {
            String nom = athlete.getNom() != null ? athlete.getNom() : "";
            String prenom = athlete.getPrenom() != null ? athlete.getPrenom() : "";
            athleteNom = (nom + " " + prenom).trim();
        }

        return ReservationSeanceDto.builder()
                .id(reservation.getId())
                .seanceId(seance != null ? seance.getId() : null)
                .theme(seance != null ? seance.getTheme() : null)
                .dateSeance(seance != null && seance.getDateSeance() != null ? seance.getDateSeance().toString() : null)
                .heureSeance(seance != null && seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : null)
                .lieu(seance != null ? seance.getLieu() : null)
                .athleteId(athlete != null ? athlete.getId() : null)
                .athleteNomComplet(athleteNom)
                .athleteEmail(athlete != null ? athlete.getEmail() : null)
                .statut(reservation.getStatut() != null ? reservation.getStatut().name() : null)
                .dateReservation(reservation.getDateReservation() != null ? reservation.getDateReservation().toString() : null)
                .build();
    }

    private ReservationSeanceDto mapToDtoForAthleteView(Seance seance, User athlete, ReservationSeance reservation) {
        String athleteNom = "";
        if (athlete != null) {
            String nom = athlete.getNom() != null ? athlete.getNom() : "";
            String prenom = athlete.getPrenom() != null ? athlete.getPrenom() : "";
            athleteNom = (nom + " " + prenom).trim();
        }

        return ReservationSeanceDto.builder()
                .id(reservation != null ? reservation.getId() : null)
                .seanceId(seance.getId())
                .theme(seance.getTheme())
                .dateSeance(seance.getDateSeance() != null ? seance.getDateSeance().toString() : null)
                .heureSeance(seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : null)
                .lieu(seance.getLieu())
                .athleteId(athlete.getId())
                .athleteNomComplet(athleteNom)
                .athleteEmail(athlete.getEmail())
                .statut(reservation != null && reservation.getStatut() != null
                        ? reservation.getStatut().name()
                        : "NON_RESERVEE")
                .dateReservation(reservation != null && reservation.getDateReservation() != null
                        ? reservation.getDateReservation().toString()
                        : null)
                .build();
    }

    private void sendSeanceEmail(Seance seance, User athlete) {
        String athleteFullName = ((athlete.getPrenom() != null ? athlete.getPrenom() : "") + " " +
                (athlete.getNom() != null ? athlete.getNom() : "")).trim();

        String coachNomComplet = "";
        String specialite = "Spécialité non définie";

        if (seance.getCoach() != null) {
            String prenomCoach = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nomCoach = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachNomComplet = (prenomCoach + " " + nomCoach).trim();

            if (seance.getCoach().getSpecialite() != null) {
                specialite = seance.getCoach().getSpecialite().getTitle();
            }
        }

        String sport = seance.getSport() != null ? seance.getSport().getTitle() : "Non défini";
        String niveau = seance.getNiveau() != null ? seance.getNiveau() : "Non défini";

        String subject = "Réservation acceptée - Académie Sportive";

        String content = """
            <div style='font-family: Arial, sans-serif; padding: 24px; color: #1f2937; background: #f9fafb;'>
                <div style='max-width: 620px; margin: auto; background: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb;'>
                    <div style='background: linear-gradient(135deg, #16a34a, #22c55e); padding: 24px; color: white;'>
                        <h1 style='margin: 0; font-size: 24px;'>Académie Sportive</h1>
                        <p style='margin: 8px 0 0; font-size: 14px; opacity: 0.95;'>Réservation confirmée</p>
                    </div>

                    <div style='padding: 28px;'>
                        <h2 style='margin-top: 0; color: #111827;'>Bonjour %s,</h2>
                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Votre réservation pour la séance suivante a été acceptée.
                        </p>

                        <div style='background: #f3f4f6; border-radius: 12px; padding: 18px; margin: 22px 0;'>
                            <p style='margin: 8px 0;'><strong>Thème :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Date :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Heure :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Lieu :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Sport :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Niveau :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Coach :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Spécialité :</strong> %s</p>
                        </div>

                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Merci de consulter votre tableau de bord pour plus d'informations.
                        </p>

                        <p style='margin-top: 28px; color: #6b7280; font-size: 14px;'>
                            Cordialement,<br>
                            <strong>L'équipe Académie Sportive</strong>
                        </p>
                    </div>
                </div>
            </div>
            """.formatted(
                athleteFullName,
                seance.getTheme(),
                seance.getDateSeance(),
                seance.getHeureSeance(),
                seance.getLieu(),
                sport,
                niveau,
                coachNomComplet,
                specialite
        );

        emailService.sendEmail(athlete.getEmail(), subject, content);
    }

    private void sendReservationRefusedEmail(Seance seance, User athlete) {
        String athleteFullName = ((athlete.getPrenom() != null ? athlete.getPrenom() : "") + " " +
                (athlete.getNom() != null ? athlete.getNom() : "")).trim();

        String subject = "Réservation refusée - Académie Sportive";

        String content = """
            <div style='font-family: Arial, sans-serif; padding: 24px; color: #1f2937; background: #f9fafb;'>
                <div style='max-width: 620px; margin: auto; background: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb;'>
                    <div style='background: linear-gradient(135deg, #dc2626, #ef4444); padding: 24px; color: white;'>
                        <h1 style='margin: 0; font-size: 24px;'>Académie Sportive</h1>
                        <p style='margin: 8px 0 0; font-size: 14px; opacity: 0.95;'>Réservation refusée</p>
                    </div>

                    <div style='padding: 28px;'>
                        <h2 style='margin-top: 0; color: #111827;'>Bonjour %s,</h2>
                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Votre réservation pour la séance <strong>%s</strong> a été refusée.
                        </p>
                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Consultez votre tableau de bord pour voir les autres séances disponibles.
                        </p>

                        <p style='margin-top: 28px; color: #6b7280; font-size: 14px;'>
                            Cordialement,<br>
                            <strong>L'équipe Académie Sportive</strong>
                        </p>
                    </div>
                </div>
            </div>
            """.formatted(
                athleteFullName,
                seance.getTheme()
        );

        emailService.sendEmail(athlete.getEmail(), subject, content);
    }
}
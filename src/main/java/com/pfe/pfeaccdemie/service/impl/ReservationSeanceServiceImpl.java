package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.ReservationSeanceDto;
import com.pfe.pfeaccdemie.entities.Paiement;
import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.enums.PaymentType;
import com.pfe.pfeaccdemie.repositories.PaiementRepository;
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

    private static final int MAX_ATHLETES_PAR_COACH_ET_SEANCE = 20;

    private final ReservationSeanceRepository reservationSeanceRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PaiementRepository paiementRepository;

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
                    ReservationSeance existing = null;

                    if (seance.getCoach() != null) {
                        existing = reservationSeanceRepository
                                .findBySeanceIdAndAthleteIdAndCoachId(
                                        seance.getId(),
                                        athlete.getId(),
                                        seance.getCoach().getId()
                                )
                                .orElse(null);
                    }

                    return mapToDtoForAthleteView(seance, athlete, existing);
                })
                .toList();
    }

    @Override
    public ReservationSeanceDto reserverSeance(Long seanceId, Long coachId, String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (athlete.getRole() != Role.ATHLETE) {
            throw new RuntimeException("Seul un athlète peut réserver une séance");
        }

        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if ("ANNULEE".equalsIgnoreCase(seance.getStatut())) {
            throw new RuntimeException("Cette séance est annulée");
        }

        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (coach.getRole() != Role.COACH) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un coach");
        }

        if (athlete.getSport() == null || seance.getSport() == null) {
            throw new RuntimeException("Sport non défini pour l'athlète ou la séance");
        }

        if (!athlete.getSport().getId().equals(seance.getSport().getId())) {
            throw new RuntimeException("Cette séance ne correspond pas au sport de l'athlète");
        }

        boolean dejaReserve = reservationSeanceRepository
                .existsByAthleteIdAndCoachIdAndSeanceId(athlete.getId(), coachId, seanceId);

        if (dejaReserve) {
            throw new RuntimeException("Vous avez déjà une réservation pour cette séance avec ce coach");
        }

        boolean hasPaidSeance = paiementRepository.existsByAthleteIdAndSeance_IdAndStatus(
                athlete.getId(),
                seance.getId(),
                PaymentStatus.PAID
        );

        boolean hasPendingCashSeance = paiementRepository.existsByAthleteIdAndSeance_IdAndStatus(
                athlete.getId(),
                seance.getId(),
                PaymentStatus.PENDING_CASH
        );

        boolean hasActiveSubscription =
                hasActiveSubscriptionForType(athlete.getId(), seance, PaymentType.MENSUEL) ||
                        hasActiveSubscriptionForType(athlete.getId(), seance, PaymentType.SEMESTRE) ||
                        hasActiveSubscriptionForType(athlete.getId(), seance, PaymentType.ANNUEL);

        if (!hasPaidSeance && !hasPendingCashSeance && !hasActiveSubscription) {
            throw new RuntimeException(
                    "Vous devez d'abord effectuer le paiement de cette séance ou disposer d'un abonnement actif."
            );
        }

        long nombreReservations = reservationSeanceRepository
                .countByCoachIdAndSeanceIdAndStatut(coachId, seanceId, StatutReservation.ACCEPTEE);

        if (nombreReservations >= MAX_ATHLETES_PAR_COACH_ET_SEANCE) {
            throw new RuntimeException("Cette séance est complète pour ce coach");
        }

        ReservationSeance reservation = ReservationSeance.builder()
                .seance(seance)
                .athlete(athlete)
                .coach(coach)
                .statut(StatutReservation.EN_ATTENTE)
                .dateReservation(LocalDateTime.now())
                .build();

        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        return mapToDto(saved);
    }

    private boolean hasActiveSubscriptionForType(Long athleteId, Seance seance, PaymentType paymentType) {
        List<Paiement> paiements = paiementRepository.findByAthleteIdAndStatusAndPaymentTypeOrderByCreatedAtDesc(
                athleteId,
                PaymentStatus.PAID,
                paymentType
        );

        if (paiements.isEmpty()) {
            return false;
        }

        LocalDateTime seanceDateTime = LocalDateTime.of(
                seance.getDateSeance(),
                seance.getHeureSeance()
        );

        for (Paiement paiement : paiements) {
            if (paiement.getCreatedAt() == null) {
                continue;
            }

            LocalDateTime start = paiement.getCreatedAt();
            LocalDateTime end = switch (paymentType) {
                case MENSUEL -> start.plusMonths(1);
                case SEMESTRE -> start.plusMonths(6);
                case ANNUEL -> start.plusYears(1);
                default -> start;
            };

            boolean coversSeanceDate =
                    !seanceDateTime.isBefore(start) &&
                            !seanceDateTime.isAfter(end);

            if (coversSeanceDate) {
                return true;
            }
        }

        return false;
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
    public List<ReservationSeanceDto> getReservationsByCoachAndSeance(Long coachId, Long seanceId) {
        seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        return reservationSeanceRepository.findByCoachIdAndSeanceId(coachId, seanceId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public long countReservationsByCoachAndSeance(Long coachId, Long seanceId) {
        return reservationSeanceRepository
                .countByCoachIdAndSeanceIdAndStatut(coachId, seanceId, StatutReservation.ACCEPTEE);
    }

    @Override
    public boolean isSeanceCompleteForCoach(Long coachId, Long seanceId) {
        return reservationSeanceRepository
                .countByCoachIdAndSeanceIdAndStatut(coachId, seanceId, StatutReservation.ACCEPTEE)
                >= MAX_ATHLETES_PAR_COACH_ET_SEANCE;
    }

    @Override
    public ReservationSeanceDto accepterReservation(Long reservationId) {
        ReservationSeance reservation = reservationSeanceRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        Seance seance = reservation.getSeance();
        User athlete = reservation.getAthlete();

        boolean isPaid = paiementRepository.existsByAthleteIdAndSeance_IdAndStatus(
                athlete.getId(),
                seance.getId(),
                PaymentStatus.PAID
        );

        if (!isPaid) {
            throw new RuntimeException(
                    "This reservation cannot be accepted because the payment is still pending admin confirmation."
            );
        }

        if ("ANNULEE".equalsIgnoreCase(seance.getStatut())) {
            throw new RuntimeException("Impossible d'accepter: cette séance est annulée");
        }

        long nombreReservationsAcceptees = reservationSeanceRepository
                .countByCoachIdAndSeanceIdAndStatut(
                        reservation.getCoach().getId(),
                        seance.getId(),
                        StatutReservation.ACCEPTEE
                );

        if (nombreReservationsAcceptees >= MAX_ATHLETES_PAR_COACH_ET_SEANCE) {
            throw new RuntimeException("Impossible d'accepter: cette séance est déjà complète pour ce coach");
        }

        reservation.setStatut(StatutReservation.ACCEPTEE);
        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        sendSeanceEmail(saved.getSeance(), saved.getAthlete(), saved.getCoach());

        return mapToDto(saved);
    }

    @Override
    public ReservationSeanceDto refuserReservation(Long reservationId) {
        ReservationSeance reservation = reservationSeanceRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation introuvable"));

        reservation.setStatut(StatutReservation.REFUSEE);
        ReservationSeance saved = reservationSeanceRepository.save(reservation);

        sendReservationRefusedEmail(saved.getSeance(), saved.getAthlete(), saved.getCoach());

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
        User coach = reservation.getCoach();

        String athleteNom = "";
        if (athlete != null) {
            String nom = athlete.getNom() != null ? athlete.getNom() : "";
            String prenom = athlete.getPrenom() != null ? athlete.getPrenom() : "";
            athleteNom = (nom + " " + prenom).trim();
        }

        String coachNom = "";
        if (coach != null) {
            String nom = coach.getNom() != null ? coach.getNom() : "";
            String prenom = coach.getPrenom() != null ? coach.getPrenom() : "";
            coachNom = (nom + " " + prenom).trim();
        }

        long nombreReservationsAcceptees = (coach != null && seance != null)
                ? reservationSeanceRepository.countByCoachIdAndSeanceIdAndStatut(
                coach.getId(),
                seance.getId(),
                StatutReservation.ACCEPTEE
        )
                : 0;

        boolean complet = nombreReservationsAcceptees >= MAX_ATHLETES_PAR_COACH_ET_SEANCE;

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
                .coachId(coach != null ? coach.getId() : null)
                .coachNomComplet(coachNom)
                .statut(reservation.getStatut() != null ? reservation.getStatut().name() : null)
                .dateReservation(reservation.getDateReservation() != null ? reservation.getDateReservation().toString() : null)
                .nombreAthletesCoachEtSeance(nombreReservationsAcceptees)
                .complet(complet)
                .build();
    }

    private ReservationSeanceDto mapToDtoForAthleteView(Seance seance, User athlete, ReservationSeance reservation) {
        String athleteNom = "";
        if (athlete != null) {
            String nom = athlete.getNom() != null ? athlete.getNom() : "";
            String prenom = athlete.getPrenom() != null ? athlete.getPrenom() : "";
            athleteNom = (nom + " " + prenom).trim();
        }

        User coach = seance.getCoach();

        String coachNom = "";
        if (coach != null) {
            String nom = coach.getNom() != null ? coach.getNom() : "";
            String prenom = coach.getPrenom() != null ? coach.getPrenom() : "";
            coachNom = (nom + " " + prenom).trim();
        }

        long nombreReservationsAcceptees = coach != null
                ? reservationSeanceRepository.countByCoachIdAndSeanceIdAndStatut(
                coach.getId(),
                seance.getId(),
                StatutReservation.ACCEPTEE
        )
                : 0;

        boolean complet = nombreReservationsAcceptees >= MAX_ATHLETES_PAR_COACH_ET_SEANCE;

        boolean annulee = "ANNULEE".equalsIgnoreCase(seance.getStatut());
        boolean annuleeMoinsDe24h = false;
        String messageAnnulation = null;

        if (annulee && seance.getDateSeance() != null && seance.getHeureSeance() != null) {
            LocalDateTime dateHeureSeance = LocalDateTime.of(
                    seance.getDateSeance(),
                    seance.getHeureSeance()
            );

            annuleeMoinsDe24h = LocalDateTime.now().isAfter(dateHeureSeance.minusHours(24));

            messageAnnulation = annuleeMoinsDe24h
                    ? "Annulée moins de 24h avant"
                    : "Cette séance a été annulée";
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
                .coachId(coach != null ? coach.getId() : null)
                .coachNomComplet(coachNom)
                .nombreAthletesCoachEtSeance(nombreReservationsAcceptees)
                .complet(complet)
                .statut(reservation != null && reservation.getStatut() != null
                        ? reservation.getStatut().name()
                        : "NON_RESERVEE")
                .seanceStatut(seance.getStatut() != null
                        ? seance.getStatut().toUpperCase().replace("É", "E")
                        : null)
                .annuleeMoinsDe24h(annuleeMoinsDe24h)
                .messageAnnulation(messageAnnulation)
                .dateReservation(reservation != null && reservation.getDateReservation() != null
                        ? reservation.getDateReservation().toString()
                        : null)
                .build();
    }

    private void sendSeanceEmail(Seance seance, User athlete, User coach) {
        String athleteFullName = ((athlete.getPrenom() != null ? athlete.getPrenom() : "") + " " +
                (athlete.getNom() != null ? athlete.getNom() : "")).trim();

        String coachNomComplet = "";
        String specialite = "Spécialité non définie";

        if (coach != null) {
            String prenomCoach = coach.getPrenom() != null ? coach.getPrenom() : "";
            String nomCoach = coach.getNom() != null ? coach.getNom() : "";
            coachNomComplet = (prenomCoach + " " + nomCoach).trim();

            if (coach.getSpecialite() != null) {
                specialite = coach.getSpecialite().getTitle();
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

    private void sendReservationRefusedEmail(Seance seance, User athlete, User coach) {
        String athleteFullName = ((athlete.getPrenom() != null ? athlete.getPrenom() : "") + " " +
                (athlete.getNom() != null ? athlete.getNom() : "")).trim();

        String coachNomComplet = "";
        if (coach != null) {
            String prenomCoach = coach.getPrenom() != null ? coach.getPrenom() : "";
            String nomCoach = coach.getNom() != null ? coach.getNom() : "";
            coachNomComplet = (prenomCoach + " " + nomCoach).trim();
        }

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
                            Votre réservation pour la séance <strong>%s</strong> avec le coach <strong>%s</strong> a été refusée.
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
                seance.getTheme(),
                coachNomComplet
        );

        emailService.sendEmail(athlete.getEmail(), subject, content);
    }
}
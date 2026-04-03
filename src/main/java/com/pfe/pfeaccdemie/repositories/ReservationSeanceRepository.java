package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.ReservationSeance;
import com.pfe.pfeaccdemie.entities.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationSeanceRepository extends JpaRepository<ReservationSeance, Long> {

    List<ReservationSeance> findBySeanceId(Long seanceId);

    List<ReservationSeance> findByAthleteId(Long athleteId);

    List<ReservationSeance> findByAthleteEmail(String email);

    List<ReservationSeance> findByCoachId(Long coachId);

    List<ReservationSeance> findByCoachIdAndSeanceId(Long coachId, Long seanceId);

    List<ReservationSeance> findBySeanceIdAndStatut(Long seanceId, StatutReservation statut);

    List<ReservationSeance> findByCoachIdAndSeanceIdAndStatut(Long coachId, Long seanceId, StatutReservation statut);

    Optional<ReservationSeance> findBySeanceIdAndAthleteId(Long seanceId, Long athleteId);

    Optional<ReservationSeance> findBySeanceIdAndAthleteIdAndCoachId(Long seanceId, Long athleteId, Long coachId);

    boolean existsByAthleteIdAndCoachIdAndSeanceId(Long athleteId, Long coachId, Long seanceId);

    long countBySeanceId(Long seanceId);

    long countBySeanceIdAndStatut(Long seanceId, StatutReservation statut);

    long countByCoachIdAndSeanceId(Long coachId, Long seanceId);

    long countByCoachIdAndSeanceIdAndStatut(Long coachId, Long seanceId, StatutReservation statut);

    void deleteBySeanceId(Long seanceId);

    void deleteByAthleteId(Long athleteId);
}
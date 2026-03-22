package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Long> {


    List<Seance> findByCoachId(Long coachId);

    List<Seance> findByCoachIdAndStatut(Long coachId, String statut);

    List<Seance> findByCoachIdAndGroupe(Long coachId, String groupe);

    List<Seance> findByCoachIdAndDateSeance(Long coachId, LocalDate dateSeance);

    List<Seance> findByCoachIdAndStatutAndGroupe(Long coachId, String statut, String groupe);
    List<Seance> findByAthletes_Email(String email);
    List<Seance> findByAthletes_Id(Long athleteId);
}
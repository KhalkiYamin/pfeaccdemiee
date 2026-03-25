package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Seance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Long> {

    List<Seance> findByCoachId(Long coachId);

    List<Seance> findByCoachIdAndStatut(Long coachId, String statut);

    List<Seance> findByCoachIdAndDateSeance(Long coachId, LocalDate dateSeance);

    List<Seance> findByCoachIdAndSportId(Long coachId, Long sportId);

    List<Seance> findByCoachIdAndStatutAndSportId(Long coachId, String statut, Long sportId);

    List<Seance> findBySportId(Long sportId);

    List<Seance> findBySportIdAndNiveau(Long sportId, String niveau);

    List<Seance> findBySportIdAndDateSeance(Long sportId, LocalDate dateSeance);
}
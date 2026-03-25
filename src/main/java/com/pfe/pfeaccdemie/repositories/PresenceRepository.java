package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Presence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findBySeanceIdAndAthleteId(Long seanceId, Long athleteId);

    List<Presence> findBySeanceId(Long seanceId);

    List<Presence> findByAthleteId(Long athleteId);

    List<Presence> findByAthleteEmail(String email);

    boolean existsBySeanceIdAndAthleteId(Long seanceId, Long athleteId);

    void deleteBySeanceId(Long seanceId);

    void deleteByAthleteId(Long athleteId);
}
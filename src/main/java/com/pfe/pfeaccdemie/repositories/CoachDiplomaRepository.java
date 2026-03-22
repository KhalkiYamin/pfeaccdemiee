package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.CoachDiploma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachDiplomaRepository extends JpaRepository<CoachDiploma, Long> {
    List<CoachDiploma> findByCoachProfile_Id(Long coachProfileId);
    void deleteByCoachProfile_Id(Long coachProfileId);
}
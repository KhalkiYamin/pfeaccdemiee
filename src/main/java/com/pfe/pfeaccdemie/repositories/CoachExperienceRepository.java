package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.CoachExperience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachExperienceRepository extends JpaRepository<CoachExperience, Long> {
    List<CoachExperience> findByCoachProfile_Id(Long coachProfileId);
    void deleteByCoachProfile_Id(Long coachProfileId);
}
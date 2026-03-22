package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.CoachReward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoachRewardRepository extends JpaRepository<CoachReward, Long> {
    List<CoachReward> findByCoachProfile_Id(Long coachProfileId);
    void deleteByCoachProfile_Id(Long coachProfileId);
}
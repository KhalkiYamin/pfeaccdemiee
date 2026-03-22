package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.CoachProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CoachProfileRepository extends JpaRepository<CoachProfile, Long> {
    Optional<CoachProfile> findByUser_Email(String email);
    Optional<CoachProfile> findByUser_Id(Long userId);
}
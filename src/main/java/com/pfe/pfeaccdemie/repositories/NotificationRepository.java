package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByAthleteIdOrderByCreatedAtDesc(Long athleteId);

    long countByAthleteIdAndReadFalse(Long athleteId);
}
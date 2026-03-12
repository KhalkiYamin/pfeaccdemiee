package com.pfe.pfeaccdemie.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfe.pfeaccdemie.entities.AppSettings;

@Repository
public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
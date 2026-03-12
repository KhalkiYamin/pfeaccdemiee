package com.pfe.pfeaccdemie.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pfe.pfeaccdemie.entities.RessourceSportif;

@Repository
public interface RessourceSportifRepository extends JpaRepository<RessourceSportif, Long> {

    List<RessourceSportif> findByDisponibilite(Boolean disponibilite);

    List<RessourceSportif> findByNomContainingIgnoreCase(String nom);
}
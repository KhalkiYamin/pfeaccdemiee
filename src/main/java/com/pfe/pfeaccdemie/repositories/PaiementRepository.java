package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {
}
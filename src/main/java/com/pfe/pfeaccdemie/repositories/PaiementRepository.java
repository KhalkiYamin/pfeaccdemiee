package com.pfe.pfeaccdemie.repositories;

import com.pfe.pfeaccdemie.entities.Paiement;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    boolean existsByAthleteIdAndSeance_IdAndStatus(
            Long athleteId,
            Long seanceId,
            PaymentStatus status
    );

    List<Paiement> findByAthleteIdAndStatusAndPaymentTypeOrderByCreatedAtDesc(
            Long athleteId,
            PaymentStatus status,
            PaymentType paymentType
    );


}
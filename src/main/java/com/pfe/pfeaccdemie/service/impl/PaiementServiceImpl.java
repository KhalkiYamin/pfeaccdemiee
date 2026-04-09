package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.PaiementRequest;
import com.pfe.pfeaccdemie.dto.PaiementResponse;
import com.pfe.pfeaccdemie.entities.Paiement;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.repositories.PaiementRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final SeanceRepository seanceRepository;

    @Override
    public PaiementResponse createPaiement(PaiementRequest request) {
        double discount = request.getDiscount() != null ? request.getDiscount() : 0.0;
        double amount = request.getAmount() != null ? request.getAmount() : 0.0;
        double finalAmount = amount - discount;

        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Séance introuvable avec id : " + request.getSeanceId()));

        String coachName = "";
        if (seance.getCoach() != null) {
            String prenom = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nom = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachName = (prenom + " " + nom).trim();
        }

        PaymentStatus status = request.getPaymentMethod() != null
                && request.getPaymentMethod().name().equals("CASH")
                ? PaymentStatus.PENDING_CASH
                : PaymentStatus.PAID;

        Paiement paiement = Paiement.builder()
                .title(seance.getTheme())
                .coach(coachName)
                .amount(amount)
                .quantity(request.getQuantity())
                .promoCode(request.getPromoCode())
                .discount(discount)
                .finalAmount(finalAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentType(request.getPaymentType())
                .status(status)
                .createdAt(LocalDateTime.now())
                .seance(seance)
                .coachId(request.getCoachId())
                .athleteId(request.getAthleteId())
                .build();

        Paiement saved = paiementRepository.save(paiement);

        return mapToResponse(saved);
    }

    @Override
    public List<PaiementResponse> getAllPaiements() {
        return paiementRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PaiementResponse getPaiementById(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable avec id : " + id));

        return mapToResponse(paiement);
    }

    private PaiementResponse mapToResponse(Paiement paiement) {
        return PaiementResponse.builder()
                .id(paiement.getId())
                .title(paiement.getTitle())
                .coach(paiement.getCoach())
                .amount(paiement.getAmount())
                .quantity(paiement.getQuantity())
                .promoCode(paiement.getPromoCode())
                .discount(paiement.getDiscount())
                .finalAmount(paiement.getFinalAmount())
                .paymentMethod(paiement.getPaymentMethod())
                .paymentType(paiement.getPaymentType())
                .status(paiement.getStatus())
                .createdAt(paiement.getCreatedAt())
                .build();
    }

}
package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.PaiementRequest;
import com.pfe.pfeaccdemie.dto.PaiementResponse;
import com.pfe.pfeaccdemie.entities.Paiement;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.repositories.PaiementRepository;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;

    @Override
    public PaiementResponse createPaiement(PaiementRequest request, Authentication authentication) {
        double discount = request.getDiscount() != null ? request.getDiscount() : 0.0;
        double amount = request.getAmount() != null ? request.getAmount() : 0.0;
        double finalAmount = amount - discount;

        Seance seance = seanceRepository.findById(request.getSeanceId())
                .orElseThrow(() -> new RuntimeException("Séance introuvable avec id : " + request.getSeanceId()));

        String coachName = "";
        if (seance.getCoach() != null) {
            String prenomCoach = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nomCoach = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachName = (prenomCoach + " " + nomCoach).trim();
        }

        String athleteName = "";
        Long athleteId = null;

        if (authentication != null && authentication.getName() != null) {
            String email = authentication.getName();

            User athlete = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Athlète introuvable avec email : " + email));

            String prenomAthlete = athlete.getPrenom() != null ? athlete.getPrenom() : "";
            String nomAthlete = athlete.getNom() != null ? athlete.getNom() : "";

            athleteName = (prenomAthlete + " " + nomAthlete).trim();
            athleteId = athlete.getId();
        }

        PaymentStatus status = request.getPaymentMethod() != null
                && request.getPaymentMethod().name().equals("CASH")
                ? PaymentStatus.PENDING_CASH
                : PaymentStatus.PAID;

        Paiement paiement = Paiement.builder()
                .title(seance.getTheme())
                .coach(coachName)
                .athleteName(athleteName)
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
                .athleteId(athleteId)
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

    @Override
    public PaiementResponse confirmCashPayment(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable avec id : " + id));

        if (paiement.getStatus() != PaymentStatus.PENDING_CASH) {
            throw new RuntimeException("Ce paiement n'est pas en attente de confirmation espèce.");
        }

        paiement.setStatus(PaymentStatus.PAID);
        Paiement saved = paiementRepository.save(paiement);

        return mapToResponse(saved);
    }

    private PaiementResponse mapToResponse(Paiement paiement) {
        return PaiementResponse.builder()
                .id(paiement.getId())
                .title(paiement.getTitle())
                .coach(paiement.getCoach())
                .athleteName(paiement.getAthleteName())
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
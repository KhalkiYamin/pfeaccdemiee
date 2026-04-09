package com.pfe.pfeaccdemie.entities;

import com.pfe.pfeaccdemie.enums.PaymentMethod;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String coach;

    private Double amount;

    private Integer quantity;

    private String promoCode;

    private Double discount;

    private Double finalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "seance_id")
    private Seance seance;

    private String provider;          // STRIPE / FLOUCI
    private String providerPaymentId; // payment_intent id / payment_id
    private String providerStatus;    // succeeded / failed / pending ...
    private String clientSecret;      // Stripe
    private String paymentUrl;        // Flouci redirect URL

    private Long coachId;
    private Long athleteId;
}
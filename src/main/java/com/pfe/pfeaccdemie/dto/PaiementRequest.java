package com.pfe.pfeaccdemie.dto;

import com.pfe.pfeaccdemie.enums.PaymentMethod;
import com.pfe.pfeaccdemie.enums.PaymentType;
import lombok.Data;

@Data
public class PaiementRequest {

    private Long seanceId;
    private Double amount;
    private Integer quantity;
    private String promoCode;
    private Double discount;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;

    private Long coachId;
    private Long athleteId;
}
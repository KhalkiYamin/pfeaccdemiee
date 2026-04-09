package com.pfe.pfeaccdemie.dto;

import com.pfe.pfeaccdemie.enums.PaymentMethod;
import com.pfe.pfeaccdemie.enums.PaymentStatus;
import com.pfe.pfeaccdemie.enums.PaymentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaiementResponse {

    private Long id;
    private String title;
    private String coach;
    private Double amount;
    private Integer quantity;
    private String promoCode;
    private Double discount;
    private Double finalAmount;
    private PaymentMethod paymentMethod;
    private PaymentType paymentType;
    private PaymentStatus status;
    private LocalDateTime createdAt;
}
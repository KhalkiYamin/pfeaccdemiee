package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.PaiementRequest;
import com.pfe.pfeaccdemie.dto.PaiementResponse;

import java.util.List;

public interface PaiementService {
    PaiementResponse createPaiement(PaiementRequest request);
    List<PaiementResponse> getAllPaiements();
    PaiementResponse getPaiementById(Long id);
}
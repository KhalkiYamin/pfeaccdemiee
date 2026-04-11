package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.PaiementRequest;
import com.pfe.pfeaccdemie.dto.PaiementResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface PaiementService {

    PaiementResponse createPaiement(PaiementRequest request, Authentication authentication);

    List<PaiementResponse> getAllPaiements();

    PaiementResponse getPaiementById(Long id);
}
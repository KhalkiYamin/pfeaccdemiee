package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.PaiementRequest;
import com.pfe.pfeaccdemie.dto.PaiementResponse;
import com.pfe.pfeaccdemie.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    public ResponseEntity<PaiementResponse> createPaiement(
            @RequestBody PaiementRequest request,
            Authentication authentication
    ) {
        PaiementResponse response = paiementService.createPaiement(request, authentication);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PaiementResponse>> getAllPaiements() {
        return ResponseEntity.ok(paiementService.getAllPaiements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaiementResponse> getPaiementById(@PathVariable Long id) {
        return ResponseEntity.ok(paiementService.getPaiementById(id));
    }
}
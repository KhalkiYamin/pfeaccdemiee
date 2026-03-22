package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;
import com.pfe.pfeaccdemie.service.AthleteDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/athlete/dashboard")
@RequiredArgsConstructor
public class AthleteDashboardController {

    private final AthleteDashboardService athleteDashboardService;

    @GetMapping("/seances")
    public ResponseEntity<List<AthleteSeanceDto>> getAthleteSeances(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(athleteDashboardService.getAthleteSeances(email));
    }
}
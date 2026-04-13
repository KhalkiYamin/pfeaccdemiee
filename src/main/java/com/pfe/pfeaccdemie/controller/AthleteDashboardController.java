package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.AthletePresenceSummaryResponse;
import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;
import com.pfe.pfeaccdemie.service.AthleteDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pfe.pfeaccdemie.dto.AthleteProfileResponse;
import java.util.List;



import com.pfe.pfeaccdemie.dto.AthleteProfileUpdateRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;





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

    @GetMapping("/presences/summary")
    public ResponseEntity<AthletePresenceSummaryResponse> getAthletePresenceSummary(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(athleteDashboardService.getAthletePresenceSummary(email));
    }
    @GetMapping("/profile")
    public ResponseEntity<AthleteProfileResponse> getAthleteProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(athleteDashboardService.getAthleteProfile(email));
    }
    @PutMapping("/profile")
    public ResponseEntity<AthleteProfileResponse> updateAthleteProfile(
            Authentication authentication,
            @RequestBody AthleteProfileUpdateRequest request) {
        String email = authentication.getName();
        return ResponseEntity.ok(athleteDashboardService.updateAthleteProfile(email, request));


    }
}
package com.pfe.pfeaccdemie.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.pfeaccdemie.dto.CoachProfileRequest;
import com.pfe.pfeaccdemie.dto.CoachProfileResponse;
import com.pfe.pfeaccdemie.service.CoachProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coach/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CoachProfileController {

    private final CoachProfileService coachProfileService;

    @GetMapping("/me")
    public CoachProfileResponse getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return coachProfileService.getMyProfile(email);
    }

    @GetMapping({"/{coachId}", "/coach/{coachId}", "/public/{coachId}"})
    public CoachProfileResponse getCoachProfileById(@PathVariable Long coachId) {
        return coachProfileService.getCoachProfileById(coachId);
    }

    @PutMapping("/me")
    public CoachProfileResponse updateMyProfile(
            Authentication authentication,
            @RequestBody CoachProfileRequest request
    ) {
        String email = authentication.getName();
        return coachProfileService.updateMyProfile(email, request);
    }
}
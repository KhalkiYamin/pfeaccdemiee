package com.pfe.pfeaccdemie.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.pfeaccdemie.dto.CoachProfileResponse;
import com.pfe.pfeaccdemie.service.CoachProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CoachPublicProfileController {

    private final CoachProfileService coachProfileService;

    @GetMapping({
            "/api/public/coaches/{coachId}/profile",
            "/api/coaches/{coachId}/profile",
            "/api/athlete/dashboard/coaches/{coachId}"
    })
    public CoachProfileResponse getCoachProfile(@PathVariable Long coachId) {
        return coachProfileService.getCoachProfileById(coachId);
    }
}
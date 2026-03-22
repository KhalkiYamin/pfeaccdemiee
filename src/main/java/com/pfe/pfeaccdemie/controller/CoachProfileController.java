package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.CoachProfileRequest;
import com.pfe.pfeaccdemie.dto.CoachProfileResponse;
import com.pfe.pfeaccdemie.service.CoachProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/me")
    public CoachProfileResponse updateMyProfile(
            Authentication authentication,
            @RequestBody CoachProfileRequest request
    ) {
        String email = authentication.getName();
        return coachProfileService.updateMyProfile(email, request);
    }
}
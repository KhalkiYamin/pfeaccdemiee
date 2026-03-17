package com.pfe.pfeaccdemie.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pfe.pfeaccdemie.dto.CoachAthleteDto;
import com.pfe.pfeaccdemie.dto.CoachProfileDto;
import com.pfe.pfeaccdemie.service.CoachDashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import com.pfe.pfeaccdemie.dto.UpdateCoachProfileDto;
import com.pfe.pfeaccdemie.dto.EvaluationDto;
import com.pfe.pfeaccdemie.dto.PresenceDto;
@RestController
@RequestMapping("/api/coach/dashboard")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CoachDashboardController {

    private final CoachDashboardService coachDashboardService;

    @GetMapping("/profile")
    public CoachProfileDto getCoachProfile(Principal principal) {
        return coachDashboardService.getCoachProfile(principal.getName());
    }

    @GetMapping("/my-athletes")
    public List<CoachAthleteDto> getMyAthletes(Principal principal) {
        return coachDashboardService.getMyAthletes(principal.getName());
    }

    @PutMapping(value = "/upload-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CoachProfileDto> uploadCoachPhoto(
            @RequestParam("image") MultipartFile image,
            Principal principal) throws IOException {
        return ResponseEntity.ok(coachDashboardService.uploadCoachPhoto(principal.getName(), image));
    }
    @PutMapping("/update-profile")
    public ResponseEntity<CoachProfileDto> updateCoachProfile(
            @RequestBody UpdateCoachProfileDto dto,
            Principal principal) {
        return ResponseEntity.ok(coachDashboardService.updateCoachProfile(principal.getName(), dto));
    }
    @GetMapping("/my-evaluations")
    public List<EvaluationDto> getMyEvaluations(Principal principal) {
        return coachDashboardService.getMyEvaluations(principal.getName());
    }
    @GetMapping("/my-presences")
    public List<PresenceDto> getMyPresences(Principal principal) {
        return coachDashboardService.getMyPresences(principal.getName());
    }

}
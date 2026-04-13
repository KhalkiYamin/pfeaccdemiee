package com.pfe.pfeaccdemie.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfe.pfeaccdemie.dto.AthleteCoachDto;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coaches")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CoachesController {

    private final UserRepository userRepository;

    @GetMapping("/approved")
    public List<AthleteCoachDto> getApprovedCoaches() {
        return userRepository.findByRoleAndEnabledAndAdminApproved(Role.COACH, true, true)
                .stream()
                .map(coach -> AthleteCoachDto.builder()
                        .id(coach.getId())
                        .nom(coach.getNom())
                        .prenom(coach.getPrenom())
                        .email(coach.getEmail())
                        .telephone(coach.getTelephone())
                        .imageProfil(coach.getImageProfil())
                        .specialite(coach.getSpecialite() != null ? coach.getSpecialite().getTitle() : "")
                        .experience(coach.getExperience() != null ? coach.getExperience() : 0)
                        .build())
                .toList();
    }
}
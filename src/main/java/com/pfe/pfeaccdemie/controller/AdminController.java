package com.pfe.pfeaccdemie.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.pfe.pfeaccdemie.dto.DashboardStatsDto;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.RessourceSportifRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RessourceSportifRepository ressourceSportifRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/athletes")
    public List<User> getAthletes() {
        return userRepository.findByRole(Role.ATHLETE);
    }

    @GetMapping("/coaches")
    public List<User> getAllCoaches() {
        return userRepository.findByRole(Role.COACH);
    }

    @GetMapping("/coaches/pending")
    public List<User> getPendingCoaches() {
        return userRepository.findByRoleAndEnabled(Role.COACH, false);
    }

    @PutMapping("/coaches/{id}/approve")
    public String approveCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        coach.setEnabled(true);
        coach.setAdminApproved(true);
        userRepository.save(coach);

        emailService.sendCoachApprovedEmail(
                coach.getEmail(),
                coach.getPrenom() + " " + coach.getNom(),
                coach.getActivationToken()
        );

        return "Coach approuvé et email envoyé";
    }

    @PutMapping("/coaches/{id}/reject")
    public String rejectCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));
        userRepository.delete(coach);
        return "Coach refusé";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        userRepository.delete(user);
        return "Utilisateur supprimé";
    }

    @GetMapping("/dashboard")
    public DashboardStatsDto getDashboardStats() {
        long totalUsers = userRepository.count();
        long totalAthletes = userRepository.findByRole(Role.ATHLETE).size();
        long totalCoaches = userRepository.findByRole(Role.COACH).size();
        long pendingCoaches = userRepository.findByRoleAndEnabled(Role.COACH, false).size();
        long totalResources = ressourceSportifRepository.count();

        return DashboardStatsDto.builder()
                .totalUsers(totalUsers)
                .totalAthletes(totalAthletes)
                .totalCoaches(totalCoaches)
                .pendingCoaches(pendingCoaches)
                .totalResources(totalResources)
                .build();
    }
}
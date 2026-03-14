package com.pfe.pfeaccdemie.controller;

import java.util.List;
import java.util.UUID;

import com.pfe.pfeaccdemie.dto.UserCreateRequest;
import com.pfe.pfeaccdemie.entities.Category;
import com.pfe.pfeaccdemie.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.pfe.pfeaccdemie.dto.DashboardStatsDto;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.RessourceSportifRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.EmailService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RessourceSportifRepository ressourceSportifRepository;

    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/users")
    public User addUser(@RequestBody UserCreateRequest req) {
        if (req.getEmail() == null || req.getEmail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email obligatoire");
        }
        if (req.getNom() == null || req.getNom().isBlank()
                || req.getPrenom() == null || req.getPrenom().isBlank()
                || req.getRole() == null || req.getRole().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Champs obligatoires manquants");
        }

        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }

        Role role;
        try {
            role = Role.valueOf(req.getRole().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide");
        }

        User user = new User();
        user.setNom(req.getNom());
        user.setPrenom(req.getPrenom());
        user.setEmail(req.getEmail());
        user.setTelephone(req.getTelephone());
        user.setRole(role);

        // Mot de passe temporaire (obligatoire car password nullable=false)
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);
        user.setPassword(passwordEncoder.encode(tempPassword));

        user.setEmailVerified(false);
        user.setActivationToken(UUID.randomUUID().toString());

        if (role == Role.ATHLETE) {
            if (req.getSportId() == null || req.getNiveau() == null || req.getNiveau().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sport et niveau obligatoires pour athlète");
            }

            Category sport = categoryRepository.findById(req.getSportId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sport introuvable"));

            user.setSport(sport);
            user.setNiveau(req.getNiveau());

            user.setEnabled(true);
            user.setAdminApproved(true);
        }

        if (role == Role.COACH) {
            if (req.getSpecialiteId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spécialité obligatoire pour coach");
            }

            Category specialite = categoryRepository.findById(req.getSpecialiteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spécialité introuvable"));

            user.setSpecialite(specialite);
            user.setExperience(req.getExperience() != null ? req.getExperience() : 0);

            user.setEnabled(false);
            user.setAdminApproved(false);
        }

        User saved = userRepository.save(user);

        emailService.sendCoachApprovedEmail(saved.getEmail(), saved.getPrenom() + " " + saved.getNom(), saved.getActivationToken());

        return saved;
    }

}
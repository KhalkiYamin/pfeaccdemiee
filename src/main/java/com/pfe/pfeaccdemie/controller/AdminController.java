package com.pfe.pfeaccdemie.controller;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.pfe.pfeaccdemie.dto.AdminUserDto;
import org.springframework.http.ResponseEntity;
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
        return userRepository.findByRoleAndEnabledAndAdminApproved(Role.COACH, false, false);
    }
    @PutMapping("/coaches/{id}/approve")
    public String approveCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        String activationToken = UUID.randomUUID().toString();

        coach.setEnabled(true);
        coach.setAdminApproved(true);
        coach.setEmailVerified(false);
        coach.setActivationToken(activationToken);

        userRepository.save(coach);

        emailService.sendCoachApprovedEmail(
                coach.getEmail(),
                coach.getPrenom() + " " + coach.getNom(),
                activationToken
        );

        return "Coach approuvé et email envoyé";
    }


    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Boolean enabled = body.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().body("Champ enabled obligatoire");
        }

        user.setEnabled(enabled);
        userRepository.save(user);

        return ResponseEntity.ok("Statut utilisateur mis à jour avec succès");
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

        // Mot de passe temporaire envoyé par email
        String tempPassword = UUID.randomUUID().toString().substring(0, 10);
        user.setPassword(passwordEncoder.encode(tempPassword));

        // Comme le compte est créé par admin, on le considère vérifié
        user.setEmailVerified(true);
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

            // Comme l'admin crée directement le compte, il est activé
            user.setEnabled(true);
            user.setAdminApproved(true);
        }

        User saved = userRepository.save(user);

        emailService.sendUserCredentialsEmail(
                saved.getEmail(),
                saved.getPrenom() + " " + saved.getNom(),
                tempPassword
        );

        return saved;
    }

    @GetMapping("/coaches/approved")
    public List<User> getApprovedCoaches() {
        return userRepository.findByRoleAndEnabledAndAdminApproved(Role.COACH, true, true);
    }

    @GetMapping("/coaches/rejected")
    public List<User> getRejectedCoaches() {
        return userRepository.findByRoleAndEnabledAndAdminApproved(Role.COACH, false, true);
    }

    @PutMapping("/coaches/{id}/reject")
    public String rejectCoach(@PathVariable Long id) {
        User coach = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        coach.setEnabled(false);
        coach.setAdminApproved(true);
        userRepository.save(coach);

        return "Coach refusé";
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody AdminUserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            var existingUser = userRepository.findByEmail(dto.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
            }
        }

        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setTelephone(dto.getTelephone());

        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            try {
                user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rôle invalide");
            }
        }

        if (user.getRole() == Role.ATHLETE) {
            if (dto.getSportId() != null) {
                Category sport = categoryRepository.findById(dto.getSportId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sport introuvable"));
                user.setSport(sport);
            }

            if (dto.getNiveau() != null && !dto.getNiveau().isBlank()) {
                user.setNiveau(dto.getNiveau());
            }
        }

        if (user.getRole() == Role.COACH) {
            if (dto.getSpecialiteId() != null) {
                Category specialite = categoryRepository.findById(dto.getSpecialiteId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Spécialité introuvable"));
                user.setSpecialite(specialite);
            }

            user.setExperience(dto.getExperience() != null ? dto.getExperience() : 0);
        }

        userRepository.save(user);

        return ResponseEntity.ok("Utilisateur modifié avec succès");
    }
}
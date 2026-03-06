package com.pfe.pfeaccdemie.service.impl;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pfe.pfeaccdemie.dto.AuthResponse;
import com.pfe.pfeaccdemie.dto.LoginRequest;
import com.pfe.pfeaccdemie.dto.RegisterRequest;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.AuthService;
import com.pfe.pfeaccdemie.service.EmailService;
import com.pfe.pfeaccdemie.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceimpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // role safe parse
        Role role = Role.ATHLETE;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rôle invalide: " + request.getRole());
            }
        }

        boolean isCoach = role == Role.COACH;
        String activationToken = UUID.randomUUID().toString();

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .role(role)
                .enabled(false)
                .emailVerified(false)
                .adminApproved(!isCoach) // ATHLETE => auto-approved, COACH => needs admin
                .activationToken(activationToken)
                // Champs COACH
                .specialite(request.getSpecialite())
                .experience(request.getExperience())
                // Champs ATHLETE
                .sport(request.getSport())
                .niveau(request.getNiveau())
                .build();

        userRepository.save(user);

        String fullName = user.getPrenom() + " " + user.getNom();

        if (isCoach) {
            return AuthResponse.builder()
                    .token(null)
                    .email(user.getEmail())
                    .nom(user.getNom())
                    .prenom(user.getPrenom())
                    .role(user.getRole())
                    .message("Compte coach créé. En attente de validation par l'admin. Vous recevrez un email après approbation.")
                    .build();
        }

        // Athlete: envoi email de vérification immédiat
        emailService.sendActivationEmail(user.getEmail(), fullName, activationToken);

        return AuthResponse.builder()
                .token(null)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .message("Inscription réussie. Veuillez vérifier votre email pour activer votre compte.")
                .build();
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Token d'activation invalide"));

        if (user.isEmailVerified()) {
            return "Votre email est déjà vérifié.";
        }

        // Pour les coachs, vérifier que l'admin a approuvé
        if (user.getRole() == Role.COACH && !user.isAdminApproved()) {
            throw new RuntimeException("Votre compte n'a pas encore été approuvé par l'administrateur.");
        }

        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setActivationToken(null);
        userRepository.save(user);

        String fullName = user.getPrenom() + " " + user.getNom();
        emailService.sendWelcomeEmail(user.getEmail(), fullName);

        return "Email vérifié avec succès. Votre compte est maintenant actif.";
    }

    @Override
    public String approveCoach(Long coachId) {
        User coach = userRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (coach.getRole() != Role.COACH) {
            throw new RuntimeException("Cet utilisateur n'est pas un coach");
        }

        if (coach.isAdminApproved()) {
            return "Ce coach est déjà approuvé.";
        }

        // Générer un nouveau token si nécessaire
        String activationToken = UUID.randomUUID().toString();
        coach.setAdminApproved(true);
        coach.setActivationToken(activationToken);
        userRepository.save(coach);

        // Envoyer l'email de vérification au coach
        String fullName = coach.getPrenom() + " " + coach.getNom();
        emailService.sendCoachApprovedEmail(coach.getEmail(), fullName, activationToken);

        return "Coach approuvé. Un email de vérification a été envoyé.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new RuntimeException("Compte non activé. Veuillez vérifier votre email.");
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Compte non activé. Veuillez vérifier votre email.");
        }

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Veuillez vérifier votre email avant de vous connecter.");
        }

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .message("Connexion réussie")
                .build();
    }
}
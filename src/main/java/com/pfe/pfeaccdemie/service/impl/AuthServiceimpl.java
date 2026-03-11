package com.pfe.pfeaccdemie.service.impl;

import java.time.LocalDateTime;
import java.util.Random;
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
import com.pfe.pfeaccdemie.entities.Category;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.CategoryRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.AuthService;
import com.pfe.pfeaccdemie.service.EmailService;
import com.pfe.pfeaccdemie.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceimpl implements AuthService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        Role role = Role.ATHLETE;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = Role.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rôle invalide : " + request.getRole());
            }
        }

        boolean isCoach = role == Role.COACH;
        boolean isAdmin = role == Role.ADMIN;

        String activationToken = UUID.randomUUID().toString();

        Category specialite = null;
        Category sport = null;

        if (isCoach && request.getSpecialiteId() != null) {
            specialite = categoryRepository.findById(request.getSpecialiteId())
                    .orElseThrow(() -> new RuntimeException("Catégorie spécialité introuvable"));
        }

        if (role == Role.ATHLETE && request.getSportId() != null) {
            sport = categoryRepository.findById(request.getSportId())
                    .orElseThrow(() -> new RuntimeException("Catégorie sport introuvable"));
        }

        User.UserBuilder userBuilder = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .telephone(request.getTelephone())
                .role(role)
                .specialite(specialite)
                .experience(request.getExperience())
                .sport(sport)
                .niveau(request.getNiveau());

        User user;

        if (isAdmin) {
            user = userBuilder
                    .enabled(true)
                    .emailVerified(true)
                    .adminApproved(true)
                    .activationToken(null)
                    .build();
        } else {
            user = userBuilder
                    .enabled(false)
                    .emailVerified(false)
                    .adminApproved(!isCoach)
                    .activationToken(activationToken)
                    .build();
        }

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

        if (role == Role.ATHLETE) {
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

        return AuthResponse.builder()
                .token(null)
                .email(user.getEmail())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .role(user.getRole())
                .message("Compte administrateur créé avec succès.")
                .build();
    }

    @Override
    public String verifyEmail(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Token d'activation invalide"));

        if (user.isEmailVerified()) {
            return "Votre email est déjà vérifié.";
        }

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

        String activationToken = UUID.randomUUID().toString();
        coach.setAdminApproved(true);
        coach.setActivationToken(activationToken);
        userRepository.save(coach);

        String fullName = coach.getPrenom() + " " + coach.getNom();
        emailService.sendCoachApprovedEmail(coach.getEmail(), fullName, activationToken);

        return "Coach approuvé. Un email de vérification a été envoyé.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        String roleValue = user.getRole() == null ? "" : user.getRole().toString().toUpperCase();
        boolean isAdmin = "ADMIN".equals(roleValue) || "ROLE_ADMIN".equals(roleValue);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (DisabledException e) {
            if (!isAdmin) {
                throw new RuntimeException("Compte non activé. Veuillez vérifier votre email.");
            }
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!isAdmin) {
            if (!user.isEnabled()) {
                throw new RuntimeException("Compte non activé. Veuillez vérifier votre email.");
            }

            if (!user.isEmailVerified()) {
                throw new RuntimeException("Veuillez vérifier votre email avant de vous connecter.");
            }
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

    @Override
    public void sendResetCode(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        String code = String.format("%06d", new Random().nextInt(1000000));

        user.setResetCode(code);
        user.setResetCodeExpiry(LocalDateTime.now().plusMinutes(10));
        userRepository.save(user);

        String fullName = user.getPrenom() + " " + user.getNom();
        String subject = "Code de réinitialisation";
        String text = "Bonjour " + fullName + ",\n\n"
                + "Votre code de réinitialisation est : " + code + "\n"
                + "Ce code expire dans 10 minutes.\n\n"
                + "Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.";

        emailService.sendEmail(user.getEmail(), subject, text);
    }
    @Override
    public void verifyResetCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        if (user.getResetCode() == null || user.getResetCodeExpiry() == null) {
            throw new RuntimeException("Aucun code trouvé");
        }

        if (!user.getResetCode().equals(code)) {
            throw new RuntimeException("Code invalide");
        }

        if (user.getResetCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code expiré");
        }
    }
    @Override
    public void resetPassword(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        if (user.getResetCode() == null || user.getResetCodeExpiry() == null) {
            throw new RuntimeException("Aucun code trouvé");
        }

        if (!user.getResetCode().equals(code)) {
            throw new RuntimeException("Code invalide");
        }

        if (user.getResetCodeExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Code expiré");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetCode(null);
        user.setResetCodeExpiry(null);

        userRepository.save(user);
    }
}
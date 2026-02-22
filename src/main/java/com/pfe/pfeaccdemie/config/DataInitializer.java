package com.pfe.pfeaccdemie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:admin@academie.com}")
    private String adminEmail;

    @Value("${admin.password:admin123}")
    private String adminPassword;

    @Value("${admin.nom:Admin}")
    private String adminNom;

    @Value("${admin.prenom:System}")
    private String adminPrenom;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdmin();
    }

    private void createDefaultAdmin() {
        // Vérifier si un admin existe déjà
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .nom(adminNom)
                    .prenom(adminPrenom)
                    .role(Role.ADMIN)
                    .enabled(true)
                    .build();

            userRepository.save(admin);
            log.info("===========================================");
            log.info("Admin par défaut créé avec succès!");
            log.info("Email: {}", adminEmail);
            log.info("Mot de passe: {}", adminPassword);
            log.info("IMPORTANT: Changez le mot de passe après la première connexion!");
            log.info("===========================================");
        } else {
            log.info("Un admin existe déjà dans le système.");
        }
    }
}
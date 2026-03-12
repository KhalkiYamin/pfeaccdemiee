package com.pfe.pfeaccdemie.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.pfe.pfeaccdemie.dto.AppSettingsDto;
import com.pfe.pfeaccdemie.dto.ChangePasswordRequest;
import com.pfe.pfeaccdemie.entities.AppSettings;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.AppSettingsRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SettingsController {

    private final AppSettingsRepository appSettingsRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public AppSettingsDto getSettings() {
        AppSettings settings = getOrCreateSettings();
        return mapToDto(settings);
    }

    @PutMapping("/academy")
    public AppSettingsDto updateAcademy(@RequestBody AppSettingsDto dto) {
        AppSettings settings = getOrCreateSettings();

        settings.setAcademyName(dto.getAcademyName());
        settings.setAcademyAddress(dto.getAcademyAddress());
        settings.setAcademyEmail(dto.getAcademyEmail());
        settings.setAcademyPhone(dto.getAcademyPhone());
        settings.setAcademyLogo(dto.getAcademyLogo());

        appSettingsRepository.save(settings);
        return mapToDto(settings);
    }

    @PutMapping("/inscription")
    public AppSettingsDto updateInscription(@RequestBody AppSettingsDto dto) {
        AppSettings settings = getOrCreateSettings();

        settings.setInscriptionActive(dto.isInscriptionActive());
        settings.setAutoApproveCoach(dto.isAutoApproveCoach());

        appSettingsRepository.save(settings);
        return mapToDto(settings);
    }

    @PutMapping("/security/session")
    public AppSettingsDto updateSession(@RequestBody AppSettingsDto dto) {
        AppSettings settings = getOrCreateSettings();

        settings.setSessionDuration(dto.getSessionDuration());

        appSettingsRepository.save(settings);
        return mapToDto(settings);
    }

    @PutMapping("/security/password")
    public String changePassword(@RequestBody ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return "Mot de passe modifié avec succès";
    }

    private AppSettings getOrCreateSettings() {
        return appSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseGet(() -> appSettingsRepository.save(
                        AppSettings.builder()
                                .academyName("Académie Sportive")
                                .academyAddress("Tunisie")
                                .academyEmail("academie@email.com")
                                .academyPhone("22111222")
                                .academyLogo("")
                                .inscriptionActive(true)
                                .autoApproveCoach(false)
                                .sessionDuration(30)
                                .build()
                ));
    }

    private AppSettingsDto mapToDto(AppSettings settings) {
        AppSettingsDto dto = new AppSettingsDto();
        dto.setId(settings.getId());
        dto.setAcademyName(settings.getAcademyName());
        dto.setAcademyAddress(settings.getAcademyAddress());
        dto.setAcademyEmail(settings.getAcademyEmail());
        dto.setAcademyPhone(settings.getAcademyPhone());
        dto.setAcademyLogo(settings.getAcademyLogo());
        dto.setInscriptionActive(settings.isInscriptionActive());
        dto.setAutoApproveCoach(settings.isAutoApproveCoach());
        dto.setSessionDuration(settings.getSessionDuration());
        return dto;
    }
}
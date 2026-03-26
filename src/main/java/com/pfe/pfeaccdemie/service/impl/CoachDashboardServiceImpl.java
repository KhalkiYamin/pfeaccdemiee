package com.pfe.pfeaccdemie.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfe.pfeaccdemie.dto.CoachAthleteDto;
import com.pfe.pfeaccdemie.dto.CoachProfileDto;
import com.pfe.pfeaccdemie.dto.EvaluationDto;
import com.pfe.pfeaccdemie.dto.PresenceDto;
import com.pfe.pfeaccdemie.dto.UpdateCoachProfileDto;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.CoachDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CoachDashboardServiceImpl implements CoachDashboardService {

    private final UserRepository userRepository;

    @Override
    public CoachProfileDto getCoachProfile(String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        List<User> athletes = getAthletesForCoach(coach);

        return CoachProfileDto.builder()
                .id(coach.getId())
                .nom(coach.getNom())
                .prenom(coach.getPrenom())
                .email(coach.getEmail())
                .telephone(coach.getTelephone())
                .imageProfil(coach.getImageProfil())
                .specialite(coach.getSpecialite() != null ? coach.getSpecialite().getTitle() : null)
                .experience(coach.getExperience())
                .totalAthletes((long) athletes.size())
                .note("4.8/5")
                .forme("Excellente")
                .seances(3)
                .succes("91%")
                .build();
    }

    @Override
    public List<CoachAthleteDto> getMyAthletes(String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        List<User> athletes = getAthletesForCoach(coach);

        String[] statuts = { "Présent", "Présente", "Absent", "En attente" };

        return athletes.stream()
                .map(a -> CoachAthleteDto.builder()
                        .id(a.getId())
                        .nomComplet(buildFullName(a))
                        .sport(a.getSport() != null ? a.getSport().getTitle() : null)
                        .niveau(a.getNiveau())
                        .statutPresence(statuts[(int) (a.getId() % statuts.length)])
                        .build())
                .collect(Collectors.toList());
    }

    private List<User> getAthletesForCoach(User coach) {
        if (coach.getSpecialite() == null) {
            return List.of();
        }

        return userRepository.findByRoleAndSport_IdAndEnabledAndAdminApproved(
                Role.ATHLETE,
                coach.getSpecialite().getId(),
                true,
                true
        );
    }

    private String buildFullName(User user) {
        String prenom = user.getPrenom() != null ? user.getPrenom() : "";
        String nom = user.getNom() != null ? user.getNom() : "";
        return (prenom + " " + nom).trim();
    }

    @Override
    public CoachProfileDto uploadCoachPhoto(String email, MultipartFile image) throws IOException {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (image == null || image.isEmpty()) {
            throw new RuntimeException("Image introuvable");
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Path uploadPath = Paths.get("uploads");

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

        coach.setImageProfil("uploads/" + fileName);
        userRepository.save(coach);

        List<User> athletes = getAthletesForCoach(coach);

        return CoachProfileDto.builder()
                .id(coach.getId())
                .nom(coach.getNom())
                .prenom(coach.getPrenom())
                .email(coach.getEmail())
                .telephone(coach.getTelephone())
                .imageProfil(coach.getImageProfil())
                .specialite(coach.getSpecialite() != null ? coach.getSpecialite().getTitle() : null)
                .experience(coach.getExperience())
                .totalAthletes((long) athletes.size())
                .note("4.8/5")
                .forme("Excellente")
                .seances(3)
                .succes("91%")
                .build();
    }

    @Override
    public CoachProfileDto updateCoachProfile(String currentEmail, UpdateCoachProfileDto dto) {
        User coach = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        if (dto.getPrenom() != null) {
            coach.setPrenom(dto.getPrenom());
        }

        if (dto.getNom() != null) {
            coach.setNom(dto.getNom());
        }

        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            coach.setEmail(dto.getEmail().trim());
        }

        if (dto.getTelephone() != null) {
            coach.setTelephone(dto.getTelephone());
        }

        if (dto.getExperience() != null) {
            coach.setExperience(dto.getExperience());
        }

        userRepository.save(coach);

        List<User> athletes = getAthletesForCoach(coach);

        return CoachProfileDto.builder()
                .id(coach.getId())
                .nom(coach.getNom())
                .prenom(coach.getPrenom())
                .email(coach.getEmail())
                .telephone(coach.getTelephone())
                .imageProfil(coach.getImageProfil())
                .specialite(coach.getSpecialite() != null ? coach.getSpecialite().getTitle() : null)
                .experience(coach.getExperience())
                .totalAthletes((long) athletes.size())
                .note("4.8/5")
                .forme("Excellente")
                .seances(3)
                .succes("91%")
                .build();
    }

    @Override
    public List<EvaluationDto> getMyEvaluations(String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        List<User> athletes = getAthletesForCoach(coach);

        return athletes.stream()
                .map(a -> EvaluationDto.builder()
                        .athleteId(a.getId())
                        .athlete(buildFullName(a))
                        .technique((int) ((a.getId() % 5) + 5))
                        .physique((int) ((a.getId() % 4) + 6))
                        .mental((int) ((a.getId() % 3) + 7))
                        .build())
                .toList();
    }

    @Override
    public List<PresenceDto> getMyPresences(String email) {
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        List<User> athletes = getAthletesForCoach(coach);

        String[] seances = { "Putting", "Approches", "Mental game", "Technique", "Cardio" };
        String[] statuses = { "Présent", "Absent", "En attente" };

        return athletes.stream()
                .map(a -> PresenceDto.builder()
                        .athleteId(a.getId())
                        .athlete(buildFullName(a))
                        .seance(seances[(int) (a.getId() % seances.length)])
                        .presence(statuses[(int) (a.getId() % statuses.length)])
                        .build())
                .toList();
    }
}
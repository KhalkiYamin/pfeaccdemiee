package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.*;
import com.pfe.pfeaccdemie.entities.*;
import com.pfe.pfeaccdemie.repositories.*;
import com.pfe.pfeaccdemie.service.CoachProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CoachProfileServiceImpl implements CoachProfileService {

    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final CoachDiplomaRepository coachDiplomaRepository;
    private final CoachExperienceRepository coachExperienceRepository;
    private final CoachRewardRepository coachRewardRepository;

    @Override
    public CoachProfileResponse getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.getRole() != Role.COACH) {
            throw new RuntimeException("Accès refusé : utilisateur non coach");
        }

        CoachProfile profile = coachProfileRepository.findByUser_Email(email)
                .orElseGet(() -> {
                    CoachProfile newProfile = CoachProfile.builder()
                            .user(user)
                            .services(new ArrayList<>())
                            .specialisations(new ArrayList<>())
                            .build();
                    return coachProfileRepository.save(newProfile);
                });

        return mapToResponse(user, profile);
    }

    @Override
    public CoachProfileResponse updateMyProfile(String email, CoachProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.getRole() != Role.COACH) {
            throw new RuntimeException("Accès refusé : utilisateur non coach");
        }

        if (request.getNom() != null) {
            user.setNom(request.getNom());
        }
        if (request.getPrenom() != null) {
            user.setPrenom(request.getPrenom());
        }
        if (request.getTelephone() != null) {
            user.setTelephone(request.getTelephone());
        }
        if (request.getImageProfil() != null) {
            user.setImageProfil(request.getImageProfil());
        }

        userRepository.save(user);

        CoachProfile profile = coachProfileRepository.findByUser_Email(email)
                .orElseGet(() -> CoachProfile.builder()
                        .user(user)
                        .services(new ArrayList<>())
                        .specialisations(new ArrayList<>())
                        .build());

        if (request.getNomUtilisateur() != null) {
            profile.setNomUtilisateur(request.getNomUtilisateur());
        }
        if (request.getGenre() != null) {
            profile.setGenre(request.getGenre());
        }
        if (request.getDateNaissance() != null) {
            profile.setDateNaissance(request.getDateNaissance());
        }
        if (request.getBiographie() != null) {
            profile.setBiographie(request.getBiographie());
        }

        if (request.getNomClub() != null) {
            profile.setNomClub(request.getNomClub());
        }
        if (request.getAdresseClub() != null) {
            profile.setAdresseClub(request.getAdresseClub());
        }
        if (request.getClubImage() != null) {
            profile.setClubImage(request.getClubImage());
        }

        if (request.getAdresseLigne1() != null) {
            profile.setAdresseLigne1(request.getAdresseLigne1());
        }
        if (request.getAdresseLigne2() != null) {
            profile.setAdresseLigne2(request.getAdresseLigne2());
        }
        if (request.getVille() != null) {
            profile.setVille(request.getVille());
        }
        if (request.getEtatProvince() != null) {
            profile.setEtatProvince(request.getEtatProvince());
        }
        if (request.getPays() != null) {
            profile.setPays(request.getPays());
        }
        if (request.getCodePostal() != null) {
            profile.setCodePostal(request.getCodePostal());
        }

        if (request.getServices() != null) {
            profile.setServices(request.getServices());
        }
        if (request.getSpecialisations() != null) {
            profile.setSpecialisations(request.getSpecialisations());
        }

        profile = coachProfileRepository.save(profile);

        coachDiplomaRepository.deleteByCoachProfile_Id(profile.getId());
        coachExperienceRepository.deleteByCoachProfile_Id(profile.getId());
        coachRewardRepository.deleteByCoachProfile_Id(profile.getId());

        if (request.getDiplomes() != null) {
            for (CoachDiplomaDto dto : request.getDiplomes()) {
                CoachDiploma diploma = CoachDiploma.builder()
                        .diplome(dto.getDiplome())
                        .ecoleInstitut(dto.getEcoleInstitut())
                        .anneeObtention(dto.getAnneeObtention())
                        .coachProfile(profile)
                        .build();
                coachDiplomaRepository.save(diploma);
            }
        }

        if (request.getExperiences() != null) {
            for (CoachExperienceDto dto : request.getExperiences()) {
                CoachExperience experience = CoachExperience.builder()
                        .nomClub(dto.getNomClub())
                        .dateDebut(dto.getDateDebut())
                        .dateFin(dto.getDateFin())
                        .poste(dto.getPoste())
                        .coachProfile(profile)
                        .build();
                coachExperienceRepository.save(experience);
            }
        }

        if (request.getRecompenses() != null) {
            for (CoachRewardDto dto : request.getRecompenses()) {
                CoachReward reward = CoachReward.builder()
                        .recompense(dto.getRecompense())
                        .annee(dto.getAnnee())
                        .coachProfile(profile)
                        .build();
                coachRewardRepository.save(reward);
            }
        }

        return mapToResponse(user, profile);
    }

    private CoachProfileResponse mapToResponse(User user, CoachProfile profile) {
        List<CoachDiplomaDto> diplomes = coachDiplomaRepository.findByCoachProfile_Id(profile.getId())
                .stream()
                .map(d -> {
                    CoachDiplomaDto dto = new CoachDiplomaDto();
                    dto.setId(d.getId());
                    dto.setDiplome(d.getDiplome());
                    dto.setEcoleInstitut(d.getEcoleInstitut());
                    dto.setAnneeObtention(d.getAnneeObtention());
                    return dto;
                })
                .toList();

        List<CoachExperienceDto> experiences = coachExperienceRepository.findByCoachProfile_Id(profile.getId())
                .stream()
                .map(e -> {
                    CoachExperienceDto dto = new CoachExperienceDto();
                    dto.setId(e.getId());
                    dto.setNomClub(e.getNomClub());
                    dto.setDateDebut(e.getDateDebut());
                    dto.setDateFin(e.getDateFin());
                    dto.setPoste(e.getPoste());
                    return dto;
                })
                .toList();

        List<CoachRewardDto> recompenses = coachRewardRepository.findByCoachProfile_Id(profile.getId())
                .stream()
                .map(r -> {
                    CoachRewardDto dto = new CoachRewardDto();
                    dto.setId(r.getId());
                    dto.setRecompense(r.getRecompense());
                    dto.setAnnee(r.getAnnee());
                    return dto;
                })
                .toList();

        return CoachProfileResponse.builder()
                .userId(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .imageProfil(user.getImageProfil())

                .nomUtilisateur(profile.getNomUtilisateur())
                .genre(profile.getGenre())
                .dateNaissance(profile.getDateNaissance())
                .biographie(profile.getBiographie())

                .nomClub(profile.getNomClub())
                .adresseClub(profile.getAdresseClub())
                .clubImage(profile.getClubImage())

                .adresseLigne1(profile.getAdresseLigne1())
                .adresseLigne2(profile.getAdresseLigne2())
                .ville(profile.getVille())
                .etatProvince(profile.getEtatProvince())
                .pays(profile.getPays())
                .codePostal(profile.getCodePostal())

                .services(profile.getServices() != null ? profile.getServices() : Collections.emptyList())
                .specialisations(profile.getSpecialisations() != null ? profile.getSpecialisations() : Collections.emptyList())

                .diplomes(diplomes)
                .experiences(experiences)
                .recompenses(recompenses)
                .build();
    }
}
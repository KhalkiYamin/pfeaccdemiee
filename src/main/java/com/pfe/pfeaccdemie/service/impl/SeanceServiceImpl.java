package com.pfe.pfeaccdemie.service.impl;
import com.pfe.pfeaccdemie.service.EmailService;
import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeanceServiceImpl implements SeanceService {

    private final SeanceRepository seanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    @Override
    public SeanceDto createSeance(SeanceDto dto) {
        User coach = userRepository.findById(dto.getCoachId())
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        Seance seance = Seance.builder()
                .theme(dto.getTheme())
                .description(dto.getDescription())
                .dateSeance(LocalDate.parse(dto.getDateSeance()))
                .heureSeance(LocalTime.parse(dto.getHeureSeance()))
                .groupe(dto.getGroupe())
                .lieu(dto.getLieu())
                .nombreAthletes(dto.getNombreAthletes())
                .statut(dto.getStatut())
                .duree(dto.getDuree())
                .objectif(dto.getObjectif())
                .coach(coach)
                .build();

        return mapToDto(seanceRepository.save(seance));
    }

    @Override
    public SeanceDto updateSeance(Long id, SeanceDto dto) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        if (dto.getCoachId() != null) {
            User coach = userRepository.findById(dto.getCoachId())
                    .orElseThrow(() -> new RuntimeException("Coach introuvable"));
            seance.setCoach(coach);
        }

        seance.setTheme(dto.getTheme());
        seance.setDescription(dto.getDescription());
        seance.setDateSeance(LocalDate.parse(dto.getDateSeance()));
        seance.setHeureSeance(LocalTime.parse(dto.getHeureSeance()));
        seance.setGroupe(dto.getGroupe());
        seance.setLieu(dto.getLieu());
        seance.setNombreAthletes(dto.getNombreAthletes());
        seance.setStatut(dto.getStatut());
        seance.setDuree(dto.getDuree());
        seance.setObjectif(dto.getObjectif());

        return mapToDto(seanceRepository.save(seance));
    }

    @Override
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    @Override
    public SeanceDto getSeanceById(Long id) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));
        return mapToDto(seance);
    }

    @Override
    public List<SeanceDto> getAllSeances() {
        return seanceRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceDto> getSeancesByCoach(Long coachId) {
        return seanceRepository.findByCoachId(coachId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeanceDto> filterSeances(Long coachId, String statut, String groupe, LocalDate dateSeance) {
        List<Seance> seances = seanceRepository.findByCoachId(coachId);

        if (statut != null && !statut.isBlank()) {
            seances = seances.stream()
                    .filter(s -> statut.equalsIgnoreCase(s.getStatut()))
                    .collect(Collectors.toList());
        }

        if (groupe != null && !groupe.isBlank()) {
            seances = seances.stream()
                    .filter(s -> groupe.equalsIgnoreCase(s.getGroupe()))
                    .collect(Collectors.toList());
        }

        if (dateSeance != null) {
            seances = seances.stream()
                    .filter(s -> dateSeance.equals(s.getDateSeance()))
                    .collect(Collectors.toList());
        }

        return seances.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private SeanceDto mapToDto(Seance seance) {
        return SeanceDto.builder()
                .id(seance.getId())
                .theme(seance.getTheme())
                .description(seance.getDescription())
                .dateSeance(seance.getDateSeance() != null ? seance.getDateSeance().toString() : null)
                .heureSeance(seance.getHeureSeance() != null ? seance.getHeureSeance().toString() : null)
                .groupe(seance.getGroupe())
                .lieu(seance.getLieu())
                .nombreAthletes(seance.getNombreAthletes())
                .statut(seance.getStatut())
                .duree(seance.getDuree())
                .objectif(seance.getObjectif())
                .coachId(seance.getCoach() != null ? seance.getCoach().getId() : null)
                .coachNom(seance.getCoach() != null ? seance.getCoach().getNom() + " " + seance.getCoach().getPrenom() : null)
                .build();
    }

    @Override
    public String assignAthleteToSeance(Long seanceId, Long athleteId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance introuvable"));

        User athlete = userRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (!"ATHLETE".equalsIgnoreCase(athlete.getRole().name())) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un athlète");
        }

        if (seance.getAthletes() == null) {
            seance.setAthletes(new java.util.ArrayList<>());
        }

        boolean alreadyAssigned = seance.getAthletes().stream()
                .anyMatch(user -> user.getId().equals(athleteId));

        if (alreadyAssigned) {
            return "Athlète déjà affecté à cette séance.";
        }

        seance.getAthletes().add(athlete);
        seanceRepository.save(seance);

        String athleteFullName = ((athlete.getPrenom() != null ? athlete.getPrenom() : "") + " "
                + (athlete.getNom() != null ? athlete.getNom() : "")).trim();

        String coachNomComplet = "";
        String specialite = "Spécialité non définie";

        if (seance.getCoach() != null) {
            String prenomCoach = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
            String nomCoach = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
            coachNomComplet = (prenomCoach + " " + nomCoach).trim();

            if (seance.getCoach().getSpecialite() != null) {
                specialite = seance.getCoach().getSpecialite().getTitle();
            }
        }

        String subject = "Nouvelle séance assignée - Académie Sportive";

        String content = """
            <div style='font-family: Arial, sans-serif; padding: 24px; color: #1f2937; background: #f9fafb;'>
                <div style='max-width: 620px; margin: auto; background: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb;'>

                    <div style='background: linear-gradient(135deg, #16a34a, #22c55e); padding: 24px; color: white;'>
                        <h1 style='margin: 0; font-size: 24px;'>Académie Sportive</h1>
                        <p style='margin: 8px 0 0; font-size: 14px; opacity: 0.95;'>Nouvelle séance programmée</p>
                    </div>

                    <div style='padding: 28px;'>
                        <h2 style='margin-top: 0; color: #111827;'>Bonjour %s,</h2>
                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Une nouvelle séance vous a été assignée avec succès. Voici les détails :
                        </p>

                        <div style='background: #f3f4f6; border-radius: 12px; padding: 18px; margin: 22px 0;'>
                            <p style='margin: 8px 0;'><strong>Thème :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Date :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Heure :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Lieu :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Coach :</strong> %s</p>
                            <p style='margin: 8px 0;'><strong>Spécialité :</strong> %s</p>
                        </div>

                        <p style='font-size: 15px; line-height: 1.7; color: #374151;'>
                            Merci de consulter votre tableau de bord pour plus d'informations et de vous présenter à l'heure prévue.
                        </p>

                        <p style='margin-top: 28px; color: #6b7280; font-size: 14px;'>
                            Cordialement,<br>
                            <strong>L'équipe Académie Sportive</strong>
                        </p>
                    </div>
                </div>
            </div>
            """.formatted(
                athleteFullName,
                seance.getTheme(),
                seance.getDateSeance(),
                seance.getHeureSeance(),
                seance.getLieu(),
                coachNomComplet,
                specialite
        );

        System.out.println("ATHLETE EMAIL = " + athlete.getEmail());
        System.out.println("BEFORE SEND EMAIL");
        emailService.sendEmail(athlete.getEmail(), subject, content);
        System.out.println("AFTER SEND EMAIL");

        return "Athlète ajouté à la séance avec succès.";
    }
}


package com.pfe.pfeaccdemie.service.impl;

import com.pfe.pfeaccdemie.dto.AthleteSeanceDto;
import com.pfe.pfeaccdemie.entities.Seance;
import com.pfe.pfeaccdemie.repositories.SeanceRepository;
import com.pfe.pfeaccdemie.service.AthleteDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteDashboardServiceImpl implements AthleteDashboardService {

    private final SeanceRepository seanceRepository;

    @Override
    public List<AthleteSeanceDto> getAthleteSeances(String email) {
        List<Seance> seances = seanceRepository.findByAthletes_Email(email);
        List<AthleteSeanceDto> result = new ArrayList<>();

        for (Seance seance : seances) {
            String coachNomComplet = "";
            String specialite = "Spécialité non définie";

            if (seance.getCoach() != null) {
                String prenom = seance.getCoach().getPrenom() != null ? seance.getCoach().getPrenom() : "";
                String nom = seance.getCoach().getNom() != null ? seance.getCoach().getNom() : "";
                coachNomComplet = (prenom + " " + nom).trim();

                if (seance.getCoach().getSpecialite() != null) {
                    specialite = seance.getCoach().getSpecialite().getTitle();
                }
            }

            AthleteSeanceDto dto = AthleteSeanceDto.builder()
                    .id(seance.getId())
                    .theme(seance.getTheme())
                    .dateSeance(seance.getDateSeance())
                    .heureSeance(seance.getHeureSeance())
                    .lieu(seance.getLieu())
                    .coachNomComplet(coachNomComplet)
                    .specialite(specialite)
                    .build();

            result.add(dto);
        }

        return result;
    }
}
package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seances")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SeanceController {

    private final SeanceService seanceService;
    private final UserRepository userRepository;

    @PostMapping
    public SeanceDto create(@RequestBody SeanceDto dto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        dto.setCoachId(coach.getId());
        return seanceService.createSeance(dto);
    }

    @GetMapping("/my-seances")
    public List<SeanceDto> getMySeances(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        return seanceService.getSeancesByCoach(coach.getId());
    }

    @GetMapping("/my-seances/filter")
    public List<SeanceDto> filterMySeances(
            Authentication authentication,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String groupe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSeance
    ) {
        if (authentication == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        return seanceService.filterSeances(coach.getId(), statut, groupe, dateSeance);
    }

    @GetMapping("/details/{id}")
    public SeanceDto getById(@PathVariable Long id) {
        return seanceService.getSeanceById(id);
    }

    @PutMapping("/{id}")
    public SeanceDto update(@PathVariable Long id, @RequestBody SeanceDto dto, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        User coach = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Coach introuvable"));

        dto.setCoachId(coach.getId());
        return seanceService.updateSeance(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        seanceService.deleteSeance(id);
    }
}
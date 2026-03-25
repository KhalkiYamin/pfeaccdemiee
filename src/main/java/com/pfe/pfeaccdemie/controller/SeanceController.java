package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.SeanceDto;
import com.pfe.pfeaccdemie.service.SeanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seances")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SeanceController {

    private final SeanceService seanceService;

    @PostMapping
    public ResponseEntity<SeanceDto> createSeance(@RequestBody SeanceDto dto) {
        return ResponseEntity.ok(seanceService.createSeance(dto));
    }

    @GetMapping
    public ResponseEntity<List<SeanceDto>> getAllSeances() {
        return ResponseEntity.ok(seanceService.getAllSeances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeanceDto> getSeanceById(@PathVariable Long id) {
        return ResponseEntity.ok(seanceService.getSeanceById(id));
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<List<SeanceDto>> getSeancesByCoach(@PathVariable Long coachId) {
        return ResponseEntity.ok(seanceService.getSeancesByCoach(coachId));
    }

    @GetMapping("/coach/{coachId}/filter")
    public ResponseEntity<List<SeanceDto>> filterSeances(
            @PathVariable Long coachId,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) String niveau,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSeance
    ) {
        return ResponseEntity.ok(seanceService.filterSeances(coachId, statut, niveau, dateSeance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SeanceDto> updateSeance(@PathVariable Long id, @RequestBody SeanceDto dto) {
        return ResponseEntity.ok(seanceService.updateSeance(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSeance(@PathVariable Long id) {
        seanceService.deleteSeance(id);
        return ResponseEntity.ok("Séance supprimée avec succès");
    }
}
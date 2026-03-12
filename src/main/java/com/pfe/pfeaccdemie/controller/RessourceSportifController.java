package com.pfe.pfeaccdemie.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;

import com.pfe.pfeaccdemie.dto.RessourceSportifDto;
import com.pfe.pfeaccdemie.entities.RessourceSportif;
import com.pfe.pfeaccdemie.repositories.RessourceSportifRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ressources-sportives")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class RessourceSportifController {

    private final RessourceSportifRepository ressourceSportifRepository;

    @GetMapping
    public List<RessourceSportifDto> getAllRessources(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean disponibilite
    ) {
        List<RessourceSportif> ressources = ressourceSportifRepository.findAll();

        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.toLowerCase();
            ressources = ressources.stream()
                    .filter(r ->
                            r.getNom().toLowerCase().contains(keyword) ||
                                    r.getDescription().toLowerCase().contains(keyword)
                    )
                    .collect(Collectors.toList());
        }

        if (disponibilite != null) {
            ressources = ressources.stream()
                    .filter(r -> r.getDisponibilite().equals(disponibilite))
                    .collect(Collectors.toList());
        }

        return ressources.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RessourceSportifDto getRessourceById(@PathVariable Long id) {
        RessourceSportif ressource = ressourceSportifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable"));

        return mapToDto(ressource);
    }

    @PostMapping
    public RessourceSportifDto createRessource(@RequestBody RessourceSportifDto dto) {
        RessourceSportif ressource = RessourceSportif.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .disponibilite(dto.getDisponibilite() != null ? dto.getDisponibilite() : true)
                .image(dto.getImage())
                .build();

        return mapToDto(ressourceSportifRepository.save(ressource));
    }

    @PutMapping("/{id}")
    public RessourceSportifDto updateRessource(@PathVariable Long id, @RequestBody RessourceSportifDto dto) {
        RessourceSportif ressource = ressourceSportifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable"));

        ressource.setNom(dto.getNom());
        ressource.setDescription(dto.getDescription());
        ressource.setDisponibilite(dto.getDisponibilite());
        ressource.setImage(dto.getImage());

        return mapToDto(ressourceSportifRepository.save(ressource));
    }

    @DeleteMapping("/{id}")
    public String deleteRessource(@PathVariable Long id) {
        RessourceSportif ressource = ressourceSportifRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource introuvable"));

        ressourceSportifRepository.delete(ressource);
        return "Ressource supprimée avec succès";
    }

    private RessourceSportifDto mapToDto(RessourceSportif ressource) {
        RessourceSportifDto dto = new RessourceSportifDto();
        dto.setId(ressource.getId());
        dto.setNom(ressource.getNom());
        dto.setDescription(ressource.getDescription());
        dto.setDisponibilite(ressource.getDisponibilite());
        dto.setImage(ressource.getImage());
        return dto;
    }
}
package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String theme;

    @Column(length = 1000)
    private String description;

    private LocalDate dateSeance;

    private LocalTime heureSeance;

    private String lieu;

    private String statut;

    private String duree;

    @Column(length = 1000)
    private String objectif;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach;

    @ManyToOne
    @JoinColumn(name = "sport_id")
    private Category sport;

    private String niveau;

    @ManyToMany
    @JoinTable(
            name = "seance_ressources",
            joinColumns = @JoinColumn(name = "seance_id"),
            inverseJoinColumns = @JoinColumn(name = "ressource_id")
    )
    @Builder.Default
    private List<RessourceSportif> ressources = new ArrayList<>();
}
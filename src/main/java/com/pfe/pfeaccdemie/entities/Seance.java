package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private String groupe;

    private String lieu;

    private Integer nombreAthletes;

    private String statut; // Planifiée, Confirmée, En cours, Terminée, Annulée

    private String duree;

    @Column(length = 1000)
    private String objectif;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private User coach;


    @ManyToMany
    @JoinTable(
            name = "seance_athletes",
            joinColumns = @JoinColumn(name = "seance_id"),
            inverseJoinColumns = @JoinColumn(name = "athlete_id")
    )
    private List<User> athletes;
}
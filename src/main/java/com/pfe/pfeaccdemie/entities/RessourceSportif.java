package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ressources_sportives")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RessourceSportif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean disponibilite = true;
    private String image;
}
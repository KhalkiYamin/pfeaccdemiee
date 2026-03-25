package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "presences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @ManyToOne
    @JoinColumn(name = "athlete_id", nullable = false)
    private User athlete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPresence statut;
}
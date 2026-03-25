package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_seance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSeance {

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
    private StatutReservation statut;

    private LocalDateTime dateReservation;
}
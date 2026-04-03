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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "athlete_id", nullable = false)
    private User athlete;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_id", nullable = false)
    private User coach;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutReservation statut;

    @Column(nullable = false)
    private LocalDateTime dateReservation;

    @PrePersist
    public void prePersist() {
        if (dateReservation == null) {
            dateReservation = LocalDateTime.now();
        }
        if (statut == null) {
            statut = StatutReservation.EN_ATTENTE;
        }
    }
}
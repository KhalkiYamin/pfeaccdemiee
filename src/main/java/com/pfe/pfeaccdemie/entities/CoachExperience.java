package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coach_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomClub;
    private String dateDebut;
    private String dateFin;
    private String poste;

    @ManyToOne
    @JoinColumn(name = "coach_profile_id")
    private CoachProfile coachProfile;
}
package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coach_diplomas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachDiploma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diplome;
    private String ecoleInstitut;
    private String anneeObtention;

    @ManyToOne
    @JoinColumn(name = "coach_profile_id")
    private CoachProfile coachProfile;
}
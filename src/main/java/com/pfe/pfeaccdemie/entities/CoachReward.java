package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "coach_rewards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recompense;
    private String annee;

    @ManyToOne
    @JoinColumn(name = "coach_profile_id")
    private CoachProfile coachProfile;
}
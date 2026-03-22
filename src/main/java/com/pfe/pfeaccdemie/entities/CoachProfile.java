package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coach_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String nomUtilisateur;
    private String genre;
    private LocalDate dateNaissance;

    @Column(length = 2000)
    private String biographie;

    private String nomClub;
    private String adresseClub;
    private String clubImage;

    private String adresseLigne1;
    private String adresseLigne2;
    private String ville;
    private String etatProvince;
    private String pays;
    private String codePostal;

    @ElementCollection
    @CollectionTable(name = "coach_profile_services", joinColumns = @JoinColumn(name = "coach_profile_id"))
    @Column(name = "service")
    @Builder.Default
    private List<String> services = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "coach_profile_specialisations", joinColumns = @JoinColumn(name = "coach_profile_id"))
    @Column(name = "specialisation")
    @Builder.Default
    private List<String> specialisations = new ArrayList<>();
}
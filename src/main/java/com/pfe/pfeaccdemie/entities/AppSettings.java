package com.pfe.pfeaccdemie.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "app_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String academyName;
    private String academyAddress;
    private String academyEmail;
    private String academyPhone;
    private String academyLogo;

    @Builder.Default
    private boolean inscriptionActive = true;

    @Builder.Default
    private boolean autoApproveCoach = false;

    @Builder.Default
    private Integer sessionDuration = 30;
}
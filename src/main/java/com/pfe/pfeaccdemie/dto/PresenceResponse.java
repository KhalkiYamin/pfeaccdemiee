package com.pfe.pfeaccdemie.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PresenceResponse {
    private Long athleteId;
    private String nomComplet;
    private String statut;
}
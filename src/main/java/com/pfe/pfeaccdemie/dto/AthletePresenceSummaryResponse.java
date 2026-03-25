package com.pfe.pfeaccdemie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AthletePresenceSummaryResponse {
    private int presenceRate;
    private String presenceLabel;
    private long presentCount;
    private long absentCount;
    private long retardCount;
    private long totalSeances;
}
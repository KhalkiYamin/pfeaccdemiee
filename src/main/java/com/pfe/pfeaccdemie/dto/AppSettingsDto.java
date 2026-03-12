package com.pfe.pfeaccdemie.dto;

import lombok.Data;

@Data
public class AppSettingsDto {
    private Long id;
    private String academyName;
    private String academyAddress;
    private String academyEmail;
    private String academyPhone;
    private String academyLogo;
    private boolean inscriptionActive;
    private boolean autoApproveCoach;
    private Integer sessionDuration;
}
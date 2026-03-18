package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.dto.SeanceDto;

import java.time.LocalDate;
import java.util.List;

public interface EmailService {

    void sendActivationEmail(String to, String fullName, String activationToken);

    void sendWelcomeEmail(String to, String fullName);

    void sendCoachApprovedEmail(String to, String fullName, String activationToken);

    void sendEmail(String to, String subject, String text);
    void sendUserCredentialsEmail(String toEmail, String fullName, String rawPassword);

    interface SeanceService {

        SeanceDto createSeance(SeanceDto dto);

        SeanceDto updateSeance(Long id, SeanceDto dto);

        void deleteSeance(Long id);

        SeanceDto getSeanceById(Long id);

        List<SeanceDto> getAllSeances();

        List<SeanceDto> getSeancesByCoach(Long coachId);

        List<SeanceDto> filterSeances(Long coachId, String statut, String groupe, LocalDate dateSeance);
    }
}
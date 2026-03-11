package com.pfe.pfeaccdemie.service;

public interface EmailService {

    void sendActivationEmail(String to, String fullName, String activationToken);

    void sendWelcomeEmail(String to, String fullName);

    void sendCoachApprovedEmail(String to, String fullName, String activationToken);

    void sendEmail(String to, String subject, String text);
}
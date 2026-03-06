package com.pfe.pfeaccdemie.service;


    public interface EmailService {
        void sendActivationEmail(String toEmail, String fullName, String activationToken);
        void sendCoachApprovedEmail(String toEmail, String fullName, String activationToken);
        void sendWelcomeEmail(String toEmail, String fullName);
    }

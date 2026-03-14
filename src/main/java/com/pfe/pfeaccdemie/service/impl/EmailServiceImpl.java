package com.pfe.pfeaccdemie.service.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.pfe.pfeaccdemie.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendActivationEmail(String toEmail, String fullName, String activationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Activation de votre compte - Académie Sportive");

            String activationLink = frontendUrl + "/pages/verify-email?token=" + activationToken;
            String emailContent = buildActivationEmailTemplate(fullName, activationLink);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("Email d'activation envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email d'activation à: {}", toEmail, e);
            throw new RuntimeException("Échec de l'envoi de l'email d'activation", e);
        }
    }

    @Override
    public void sendCoachApprovedEmail(String toEmail, String fullName, String activationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Votre compte coach a été approuvé - Académie Sportive");

            String activationLink = frontendUrl + "/pages/verify-email?token=" + activationToken;
            String emailContent = buildCoachApprovedTemplate(fullName, activationLink);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("Email d'approbation coach envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email d'approbation à: {}", toEmail, e);
            throw new RuntimeException("Échec de l'envoi de l'email d'approbation", e);
        }
    }

    @Override
    public void sendEmail(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            logger.info("Email envoyé à: {}", to);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email à: {}", to, e);
            throw new RuntimeException("Échec de l'envoi de l'email", e);
        }
    }
    @Override
    public void sendWelcomeEmail(String toEmail, String fullName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Bienvenue sur l'Académie Sportive !");

            String emailContent = buildWelcomeEmailTemplate(fullName);
            helper.setText(emailContent, true);

            mailSender.send(message);
            logger.info("Email de bienvenue envoyé à: {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Erreur lors de l'envoi de l'email de bienvenue à: {}", toEmail, e);
        }
    }

    // ======================== TEMPLATES ========================

    private String buildActivationEmailTemplate(String fullName, String activationLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #1e3a5f 0%%, #2d5986 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 14px 35px; background: #1e3a5f; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }
                    .footer { text-align: center; color: #6b7280; font-size: 14px; margin-top: 30px; }
                    .info-box { background: #dbeafe; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #3b82f6; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Vérification de votre email</h1>
                        <p>Académie Sportive</p>
                    </div>
                    <div class="content">
                        <h2>Bonjour %s,</h2>
                        <p>Merci de vous être inscrit sur notre plateforme Académie Sportive ! Pour activer votre compte, veuillez vérifier votre adresse email en cliquant sur le bouton ci-dessous :</p>

                        <div style="text-align: center;">
                            <a href="%s" class="btn">Activer mon compte</a>
                        </div>

                        <div class="info-box">
                            <p><strong>Information :</strong> Ce lien d'activation est valide pendant 24 heures. Après ce délai, vous devrez demander un nouveau lien.</p>
                        </div>

                        <p style="color: #6b7280; font-size: 14px;">Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :<br>
                        <a href="%s">%s</a></p>

                        <p style="margin-top: 30px;">À bientôt sur l'Académie Sportive !</p>
                    </div>
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>&copy; 2026 Académie Sportive. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, activationLink, activationLink, activationLink);
    }

    private String buildCoachApprovedTemplate(String fullName, String activationLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #059669 0%%, #10b981 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 14px 35px; background: #059669; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }
                    .footer { text-align: center; color: #6b7280; font-size: 14px; margin-top: 30px; }
                    .success-box { background: #d1fae5; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #059669; }
                    .info-box { background: #dbeafe; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #3b82f6; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Compte Coach Approuvé !</h1>
                        <p>Académie Sportive</p>
                    </div>
                    <div class="content">
                        <h2>Bonjour %s,</h2>

                        <div class="success-box">
                            <p><strong>Bonne nouvelle !</strong> Votre compte coach a été vérifié et approuvé par notre équipe d'administration.</p>
                        </div>

                        <p>Pour finaliser l'activation de votre compte, veuillez vérifier votre adresse email en cliquant sur le bouton ci-dessous :</p>

                        <div style="text-align: center;">
                            <a href="%s" class="btn">Vérifier mon email et activer mon compte</a>
                        </div>

                        <div class="info-box">
                            <p><strong>Étape finale :</strong> Une fois votre email vérifié, vous pourrez vous connecter et commencer à gérer vos athlètes.</p>
                        </div>

                        <p style="color: #6b7280; font-size: 14px;">Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :<br>
                        <a href="%s">%s</a></p>

                        <p style="margin-top: 30px;">Bienvenue dans l'équipe !</p>
                    </div>
                    <div class="footer">
                        <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                        <p>&copy; 2026 Académie Sportive. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, activationLink, activationLink, activationLink);
    }

    private String buildWelcomeEmailTemplate(String fullName) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #10b981 0%%, #059669 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }
                    .btn { display: inline-block; padding: 14px 35px; background: #10b981; color: white; text-decoration: none; border-radius: 6px; margin: 20px 0; font-weight: bold; }
                    .footer { text-align: center; color: #6b7280; font-size: 14px; margin-top: 30px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Compte activé avec succès !</h1>
                        <p>Académie Sportive</p>
                    </div>
                    <div class="content">
                        <h2>Félicitations %s,</h2>
                        <p>Votre compte a été activé avec succès ! Vous pouvez maintenant accéder à toutes les fonctionnalités de l'Académie Sportive.</p>

                        <div style="text-align: center;">
                            <a href="%s/auth/login" class="btn">Se connecter</a>
                        </div>

                        <p style="margin-top: 30px;">Bon entraînement !</p>
                    </div>
                    <div class="footer">
                        <p>&copy; 2026 Académie Sportive. Tous droits réservés.</p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, frontendUrl);
    }
    @Override
    public void sendUserCredentialsEmail(String toEmail, String fullName, String rawPassword) {
        try {
            String subject = "Création de votre compte - Académie Sportive";

            String content =
                    "<div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>"
                            + "<h2>Bienvenue " + fullName + " 👋</h2>"
                            + "<p>Un compte a été créé pour vous par l'administration.</p>"
                            + "<p><strong>Email :</strong> " + toEmail + "</p>"
                            + "<p><strong>Mot de passe temporaire :</strong> " + rawPassword + "</p>"
                            + "<p>Veuillez vous connecter avec ces identifiants.</p>"
                            + "<p>Nous vous recommandons de changer votre mot de passe après la première connexion.</p>"
                            + "<br>"
                            + "<p>Cordialement,<br>L'équipe Académie Sportive</p>"
                            + "</div>";

            sendEmail(toEmail, subject, content);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email des identifiants", e);
        }
    }


}

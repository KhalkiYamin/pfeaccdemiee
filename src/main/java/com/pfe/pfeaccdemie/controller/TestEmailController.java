package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @PostMapping("/email")
    public String sendTestEmail(@RequestParam String to) {
        String subject = "Test Email - Académie Sportive";
        String content = """
                <div style='font-family: Arial, sans-serif; padding: 20px; color: #333;'>
                    <h2>Bonjour,</h2>
                    <p>Ceci est un email de test depuis Académie Sportive.</p>
                    <p>Si vous recevez ce message, la configuration email fonctionne correctement.</p>
                    <br>
                    <p>Cordialement,<br>L'équipe Académie Sportive</p>
                </div>
                """;

        emailService.sendEmail(to, subject, content);
        return "Email de test envoyé à : " + to;
    }
}
package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.dto.ChatRequestDto;
import com.pfe.pfeaccdemie.dto.ChatResponseDto;
import com.pfe.pfeaccdemie.service.ChatbotAthleteService;
import com.pfe.pfeaccdemie.service.GroqChatService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    private final GroqChatService groqChatService;
    private final ChatbotAthleteService chatbotAthleteService;

    public ChatbotController(GroqChatService groqChatService,
                             ChatbotAthleteService chatbotAthleteService) {
        this.groqChatService = groqChatService;
        this.chatbotAthleteService = chatbotAthleteService;
    }

    @PostMapping
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return groqChatService.askChatbot(request.getMessage());
    }

    @PostMapping("/athlete")
    public ChatResponseDto chatAthlete(@RequestBody ChatRequestDto request,
                                       Authentication authentication) {
        String email = authentication.getName();
        return chatbotAthleteService.handleAthleteMessage(request.getMessage(), email);
    }
}
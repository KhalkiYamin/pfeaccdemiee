package com.pfe.pfeaccdemie.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.pfeaccdemie.dto.ChatResponseDto;

@Service
public class GroqChatService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GroqChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public ChatResponseDto askChatbot(String userMessage) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", """
                أنت مساعد ذكي خاص بأكاديمية رياضية.
                أجب دائمًا باللغة العربية بطريقة واضحة وبسيطة وودية.
                إذا كتب المستخدم بلغة أخرى يمكنك الفهم، لكن تكون الإجابة بالعربية ما لم يطلب المستخدم غير ذلك.
                اعتمد فقط على المعلومات المتوفرة لك، ولا تخترع بيانات غير موجودة.
                """);

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            List<Map<String, Object>> messages = new ArrayList<>();
            messages.add(systemMessage);
            messages.add(userMsg);

            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.7);

            HttpEntity<Map<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    groqApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getBody() == null) {
                return new ChatResponseDto("لا توجد أي إجابة من المساعد الذكي حاليًا.");
            }

            JsonNode root = objectMapper.readTree(response.getBody());
            String reply = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return new ChatResponseDto(reply);

        } catch (Exception e) {
            return new ChatResponseDto("حدث خطأ في المساعد الذكي: " + e.getMessage());
        }
    }
}
package com.pfe.pfeaccdemie.controller;

import com.pfe.pfeaccdemie.entities.Notification;
import com.pfe.pfeaccdemie.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/athlete/notifications")
@CrossOrigin("*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {
        String email = authentication.getName();
        return notificationService.getAthleteNotifications(email);
    }

    @GetMapping("/unread-count")
    public Map<String, Long> getUnreadCount(Authentication authentication) {
        String email = authentication.getName();
        long count = notificationService.getUnreadCount(email);
        return Map.of("unreadCount", count);
    }

    @PutMapping("/{id}/read")
    public Notification markAsRead(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        return notificationService.markAsRead(id, email);
    }

    @PutMapping("/read-all")
    public Map<String, String> markAllAsRead(Authentication authentication) {
        String email = authentication.getName();
        notificationService.markAllAsRead(email);
        return Map.of("message", "Toutes les notifications sont marquées comme lues");
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteNotification(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        notificationService.deleteNotification(id, email);
        return Map.of("message", "Notification supprimée avec succès");
    }

    @PostMapping("/create/{athleteId}")
    public Notification createNotification(
            @PathVariable Long athleteId,
            @RequestBody Map<String, String> request
    ) {
        return notificationService.createNotificationForAthlete(
                athleteId,
                request.get("title"),
                request.get("message"),
                request.get("type")
        );
    }
}
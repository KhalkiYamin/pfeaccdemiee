package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.entities.Notification;
import com.pfe.pfeaccdemie.entities.Role;
import com.pfe.pfeaccdemie.entities.User;
import com.pfe.pfeaccdemie.repositories.NotificationRepository;
import com.pfe.pfeaccdemie.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public List<Notification> getAthleteNotifications(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        return notificationRepository.findByAthleteIdOrderByCreatedAtDesc(athlete.getId());
    }

    @Override
    public long getUnreadCount(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        return notificationRepository.countByAthleteIdAndReadFalse(athlete.getId());
    }

    @Override
    public Notification markAsRead(Long notificationId, String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (!notification.getAthlete().getId().equals(athlete.getId())) {
            throw new RuntimeException("Accès refusé à cette notification");
        }

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        List<Notification> notifications =
                notificationRepository.findByAthleteIdOrderByCreatedAtDesc(athlete.getId());

        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(Long notificationId, String email) {
        User athlete = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (!notification.getAthlete().getId().equals(athlete.getId())) {
            throw new RuntimeException("Accès refusé à cette notification");
        }

        notificationRepository.delete(notification);
    }

    @Override
    public Notification createNotificationForAthlete(Long athleteId, String title, String message, String type) {
        User athlete = userRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlète introuvable"));

        if (athlete.getRole() != Role.ATHLETE) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un athlète");
        }

        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .type(type)
                .read(false)
                .athlete(athlete)
                .build();

        return notificationRepository.save(notification);
    }
}
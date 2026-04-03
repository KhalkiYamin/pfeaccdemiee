package com.pfe.pfeaccdemie.service;

import com.pfe.pfeaccdemie.entities.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getAthleteNotifications(String email);

    long getUnreadCount(String email);

    Notification markAsRead(Long notificationId, String email);

    void markAllAsRead(String email);

    void deleteNotification(Long notificationId, String email);

    Notification createNotificationForAthlete(Long athleteId, String title, String message, String type);
}
package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Notification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationsService {
    List<Notification> listNotifications(UUID userId);
    Notification markAsRead(UUID notificationId, UUID userId);
    void markAllRead(UUID userId);
    long unreadCount(UUID userId);
    void updatePreferences(UUID userId, Map<String, Object> prefs);
}

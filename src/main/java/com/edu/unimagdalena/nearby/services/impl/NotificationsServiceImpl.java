package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Notification;
import com.edu.unimagdalena.nearby.repositories.NotificationRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.services.NotificationsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class NotificationsServiceImpl implements NotificationsService {

    private final NotificationRepository notificationRepo;
    private final CuentaUsuarioRepository cuentaRepo;

    public NotificationsServiceImpl(NotificationRepository notificationRepo,
                                    CuentaUsuarioRepository cuentaRepo) {
        this.notificationRepo = notificationRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public List<Notification> listNotifications(UUID userId) {
        return notificationRepo.findByRecipient_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Notification markAsRead(UUID notificationId, UUID userId) {
        Notification n = notificationRepo.findById(notificationId).orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
        if (!n.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("No autorizado para modificar esta notificación");
        }
        n.setRead(true);
        return notificationRepo.save(n);
    }

    @Override
    public void markAllRead(UUID userId) {
        List<Notification> list = notificationRepo.findByRecipient_IdOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            if (!n.isRead()) {
                n.setRead(true);
            }
        }
        notificationRepo.saveAll(list);
    }

    @Override
    public long unreadCount(UUID userId) {
        return notificationRepo.countByRecipient_IdAndReadFalse(userId);
    }

    @Override
    public void updatePreferences(UUID userId, Map<String, Object> prefs) {
        // Simulación: no se persisten preferencias en entidad actual.
        // Si se desea persistir, añadir columna/preferences entity.
        // Validación mínima: usuario existe
        cuentaRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        // no-op: aceptar el payload
    }
}

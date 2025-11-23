package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(UUID recipientId);
    long countByRecipient_IdAndReadFalse(UUID recipientId);
}

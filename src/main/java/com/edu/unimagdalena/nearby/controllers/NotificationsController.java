package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Notification;
import com.edu.unimagdalena.nearby.services.NotificationsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para notificaciones.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationsController {

	private final NotificationsService notificationsService;

	public NotificationsController(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	@GetMapping
	public ResponseEntity<?> listNotifications(@RequestParam String userId) {
		try {
			List<Notification> list = notificationsService.listNotifications(UUID.fromString(userId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	@PutMapping("/{id}/read")
	public ResponseEntity<?> markNotificationRead(@PathVariable String id, @RequestParam String userId) {
		try {
			Notification n = notificationsService.markAsRead(UUID.fromString(id), UUID.fromString(userId));
			return ResponseEntity.ok(n);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/read-all")
	public ResponseEntity<?> markAllRead(@RequestParam String userId) {
		try {
			notificationsService.markAllRead(UUID.fromString(userId));
			return ResponseEntity.ok(Map.of("message", "Todas las notificaciones marcadas como leídas"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	@GetMapping("/unread-count")
	public ResponseEntity<?> unreadCount(@RequestParam String userId) {
		try {
			long c = notificationsService.unreadCount(UUID.fromString(userId));
			return ResponseEntity.ok(Map.of("unreadCount", c));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	@PutMapping("/preferences")
	public ResponseEntity<?> updatePrefs(@RequestParam String userId, @RequestBody Map<String, Object> prefs) {
		try {
			notificationsService.updatePreferences(UUID.fromString(userId), prefs);
			return ResponseEntity.ok(Map.of("message", "Preferencias actualizadas (simulado)"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}
}

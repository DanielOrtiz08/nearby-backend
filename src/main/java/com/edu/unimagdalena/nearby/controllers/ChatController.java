package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Conversacion;
import com.edu.unimagdalena.nearby.entities.Mensaje;
import com.edu.unimagdalena.nearby.services.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para mensajería y chat.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/conversations")
	public ResponseEntity<?> createConversation(@RequestBody Map<String, Object> payload) {
		try {
			Conversacion created = chatService.createConversation(payload);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/conversations")
	public ResponseEntity<?> listConversations(@RequestParam(required = false) String userId) {
		try {
			List<Conversacion> list = chatService.listConversations(userId == null ? null : UUID.fromString(userId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "userId inválido"));
		}
	}

	@GetMapping("/conversations/{id}")
	public ResponseEntity<?> getConversation(@PathVariable String id) {
		try {
			Conversacion c = chatService.getConversation(UUID.fromString(id));
			return ResponseEntity.ok(c);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/conversations/{id}/messages")
	public ResponseEntity<?> sendMessage(@PathVariable String id, @RequestBody Map<String, Object> message) {
		try {
			Mensaje created = chatService.sendMessage(UUID.fromString(id), message);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/conversations/{id}/messages")
	public ResponseEntity<?> getMessages(@PathVariable String id) {
		try {
			List<Mensaje> msgs = chatService.getMessages(UUID.fromString(id));
			return ResponseEntity.ok(msgs);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		}
	}

	@PutMapping("/messages/{id}/read")
	public ResponseEntity<?> markMessageRead(@PathVariable String id, @RequestBody(required = false) Map<String, Object> payload) {
		try {
			String readerId = payload != null && payload.get("readerId") != null ? payload.get("readerId").toString() : null;
			Mensaje updated = chatService.markMessageRead(UUID.fromString(id), readerId);
			return ResponseEntity.ok(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/messages/{id}/attachments")
	public ResponseEntity<?> uploadAttachment(@PathVariable String id, @RequestParam("file") MultipartFile file) {
		try {
			Mensaje updated = chatService.uploadAttachment(UUID.fromString(id), file);
			return ResponseEntity.status(HttpStatus.CREATED).body(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
		}
	}
}

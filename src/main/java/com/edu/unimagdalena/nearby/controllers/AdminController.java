package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Reporte;
import com.edu.unimagdalena.nearby.services.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para administración (delegan a AdminService).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@GetMapping("/users")
	public ResponseEntity<?> listUsers() {
		// Listar usuarios (admin) — respuesta sanitizada
		List<CuentaUsuario> users = adminService.listUsers();
		return ResponseEntity.ok(users);
	}

	@PutMapping("/users/{id}/status")
	public ResponseEntity<?> changeUserStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		// Cambiar estado usuario (payload: { "activo": true } )
		try {
			UUID uuid = UUID.fromString(id);
			CuentaUsuario updated = adminService.changeUserStatus(uuid, payload);
			return ResponseEntity.ok(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/properties/pending")
	public ResponseEntity<?> pendingProperties() {
		// Propiedades pendientes
		List<Propiedad> pending = adminService.pendingProperties();
		return ResponseEntity.ok(pending);
	}

	@PutMapping("/properties/{id}/approve")
	public ResponseEntity<?> approveProperty(@PathVariable String id) {
		try {
			UUID uuid = UUID.fromString(id);
			Propiedad prop = adminService.approveProperty(uuid);
			return ResponseEntity.ok(prop);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/properties/{id}/reject")
	public ResponseEntity<?> rejectProperty(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		// payload puede contener { "reason": "motivo" }
		try {
			UUID uuid = UUID.fromString(id);
			String reason = payload != null && payload.get("reason") != null ? payload.get("reason").toString() : null;
			Propiedad prop = adminService.rejectProperty(uuid, reason);
			return ResponseEntity.ok(prop);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/reports")
	public ResponseEntity<?> listReports() {
		List<Reporte> reports = adminService.listReports();
		return ResponseEntity.ok(reports);
	}

	@PostMapping("/reports/{id}/resolve")
	public ResponseEntity<?> resolveReport(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		// payload puede contener { "resolution": "texto" }
		try {
			UUID uuid = UUID.fromString(id);
			String resolution = payload != null && payload.get("resolution") != null ? payload.get("resolution").toString() : null;
			Reporte resolved = adminService.resolveReport(uuid, resolution);
			return ResponseEntity.ok(resolved);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/dashboard/stats")
	public ResponseEntity<?> dashboardStats() {
		Map<String, Object> stats = adminService.dashboardStats();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/analytics")
	public ResponseEntity<?> analytics() {
		Map<String, Object> analytics = adminService.analytics();
		return ResponseEntity.ok(analytics);
	}
}

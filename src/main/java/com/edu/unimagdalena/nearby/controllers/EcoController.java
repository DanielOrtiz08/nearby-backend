package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.EcoCertification;
import com.edu.unimagdalena.nearby.services.EcoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para certificaciones eco (stubs).
 */
@RestController
@RequestMapping("/api/eco-certifications")
public class EcoController {

	private final EcoService ecoService;

	public EcoController(EcoService ecoService) {
		this.ecoService = ecoService;
	}

	@PostMapping("/apply")
	public ResponseEntity<?> applyCertification(@RequestBody Map<String, Object> payload) {
		try {
			EcoCertification created = ecoService.applyCertification(payload);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<?> listCertifications() {
		List<EcoCertification> list = ecoService.listCertifications();
		return ResponseEntity.ok(list);
	}

	@PutMapping("/{id}/verify")
	public ResponseEntity<?> verifyCertification(@PathVariable String id) {
		try {
			EcoCertification cert = ecoService.verifyCertification(UUID.fromString(id));
			return ResponseEntity.ok(cert);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}
}

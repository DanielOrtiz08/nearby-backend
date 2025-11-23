package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.services.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para búsqueda, historial y alertas.
 */
@RestController
@RequestMapping("/api/search")
public class SearchController {

	private final SearchService searchService;

	public SearchController(SearchService searchService) {
		this.searchService = searchService;
	}

	// 86. GET /api/search/suggestions
	@GetMapping("/suggestions")
	public ResponseEntity<?> suggestions(@RequestParam(required = false) String q) {
		try {
			List<String> res = searchService.suggestions(q);
			return ResponseEntity.ok(res);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 87. POST /api/search/history
	@PostMapping("/history")
	public ResponseEntity<?> saveHistory(@RequestBody Map<String, Object> dto) {
		try {
			searchService.saveHistory(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("saved", true));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 88. GET /api/search/history
	@GetMapping("/history")
	public ResponseEntity<?> getHistory(@RequestParam String userId) {
		try {
			List<?> history = searchService.getHistory(UUID.fromString(userId));
			return ResponseEntity.ok(history);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	// 89. POST /api/search/alerts
	@PostMapping("/alerts")
	public ResponseEntity<?> createAlert(@RequestBody Map<String, Object> dto) {
		try {
			searchService.createAlert(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("created", true));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 90. GET /api/search/alerts
	@GetMapping("/alerts")
	public ResponseEntity<?> listAlerts(@RequestParam String userId) {
		try {
			List<?> list = searchService.listAlerts(UUID.fromString(userId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	// 91. DELETE /api/search/alerts/{id}
	@DeleteMapping("/alerts/{id}")
	public ResponseEntity<?> deleteAlert(@PathVariable String id, @RequestParam String userId) {
		try {
			searchService.deleteAlert(UUID.fromString(id), UUID.fromString(userId));
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
		}
	}
}

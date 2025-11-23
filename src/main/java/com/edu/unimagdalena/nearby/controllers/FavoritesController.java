package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.ListaPersonalizada;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Favorito;
import com.edu.unimagdalena.nearby.services.FavoritesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para favoritos y listas.
 */
@RestController
@RequestMapping("/api")
public class FavoritesController {

	private final FavoritesService favoritesService;

	public FavoritesController(FavoritesService favoritesService) {
		this.favoritesService = favoritesService;
	}

	@PostMapping("/favorites")
	public ResponseEntity<?> addFavorite(@RequestBody Map<String, Object> payload) {
		try {
			Favorito f = favoritesService.addFavorite(
					UUID.fromString(payload.get("studentId").toString()),
					UUID.fromString(payload.get("propertyId").toString())
			);
			return ResponseEntity.status(HttpStatus.CREATED).body(f);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/favorites/{propertyId}")
	public ResponseEntity<?> removeFavorite(@PathVariable String propertyId, @RequestParam String studentId) {
		try {
			favoritesService.removeFavorite(UUID.fromString(studentId), UUID.fromString(propertyId));
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/favorites/student/{studentId}")
	public ResponseEntity<?> favoritesByStudent(@PathVariable String studentId) {
		try {
			List<Propiedad> list = favoritesService.favoritesByStudent(UUID.fromString(studentId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		}
	}

	@PostMapping("/lists")
	public ResponseEntity<?> createList(@RequestBody Map<String, Object> payload) {
		try {
			ListaPersonalizada created = favoritesService.createList(payload);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/lists/{id}")
	public ResponseEntity<?> updateList(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		try {
			ListaPersonalizada updated = favoritesService.updateList(UUID.fromString(id), payload);
			return ResponseEntity.ok(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/lists/{id}")
	public ResponseEntity<?> deleteList(@PathVariable String id) {
		try {
			favoritesService.deleteList(UUID.fromString(id));
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/lists/{id}/properties")
	public ResponseEntity<?> addPropertyToList(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		try {
			ListaPersonalizada updated = favoritesService.addPropertyToList(UUID.fromString(id), UUID.fromString(payload.get("propertyId").toString()));
			return ResponseEntity.status(HttpStatus.CREATED).body(updated);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/lists/{id}/properties/{propertyId}")
	public ResponseEntity<?> removePropertyFromList(@PathVariable String id, @PathVariable String propertyId) {
		try {
			favoritesService.removePropertyFromList(UUID.fromString(id), UUID.fromString(propertyId));
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}
}

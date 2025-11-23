package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.services.PropertiesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para propiedades.
 */
@RestController
@RequestMapping("/api/properties")
public class PropertiesController {

	private final PropertiesService propertiesService;

	public PropertiesController(PropertiesService propertiesService) {
		this.propertiesService = propertiesService;
	}

	@PostMapping
	public ResponseEntity<?> createProperty(@RequestBody Map<String, Object> dto) {
		try {
			Propiedad created = propertiesService.createProperty(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<?> listProperties(@RequestParam(required = false) Map<String, Object> filters) {
		try {
			List<Propiedad> list = propertiesService.listProperties(filters);
			return ResponseEntity.ok(list);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getProperty(@PathVariable String id) {
		try {
			Propiedad p = propertiesService.getProperty(UUID.fromString(id));
			return ResponseEntity.ok(p);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateProperty(@PathVariable String id, @RequestBody Map<String, Object> dto) {
		try {
			Propiedad p = propertiesService.updateProperty(UUID.fromString(id), dto);
			return ResponseEntity.ok(p);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProperty(@PathVariable String id) {
		try {
			propertiesService.deleteProperty(UUID.fromString(id));
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<?> changePropertyStatus(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		try {
			String status = payload != null && payload.get("status") != null ? payload.get("status").toString() : null;
			Propiedad p = propertiesService.changePropertyStatus(UUID.fromString(id), status);
			return ResponseEntity.ok(p);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<?> propertiesByOwner(@PathVariable String ownerId) {
		try {
			List<Propiedad> list = propertiesService.propertiesByOwner(UUID.fromString(ownerId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		}
	}

	@PostMapping("/{id}/photos")
	public ResponseEntity<?> addPropertyPhotos(@PathVariable String id, @RequestParam("files") MultipartFile[] files) {
		try {
			List<String> added = propertiesService.addPropertyPhotos(UUID.fromString(id), files);
			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("added", added));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}/photos/{photoId}")
	public ResponseEntity<?> deletePropertyPhoto(@PathVariable String id, @PathVariable String photoId) {
		try {
			// photoId expected to be the URL stored; client should send the exact url
			propertiesService.deletePropertyPhoto(UUID.fromString(id), photoId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/search")
	public ResponseEntity<?> advancedSearch(@RequestParam Map<String, Object> q) {
		try {
			List<Propiedad> res = propertiesService.advancedSearch(q);
			return ResponseEntity.ok(res);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/near-university")
	public ResponseEntity<?> propertiesNearUniversity(@RequestParam String universityId) {
		try {
			List<Propiedad> res = propertiesService.propertiesNearUniversity(UUID.fromString(universityId));
			return ResponseEntity.ok(res);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "universityId inválido"));
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/featured")
	public ResponseEntity<?> featuredProperties() {
		List<Propiedad> res = propertiesService.featuredProperties();
		return ResponseEntity.ok(res);
	}

	@GetMapping("/similar/{propertyId}")
	public ResponseEntity<?> similarProperties(@PathVariable String propertyId) {
		try {
			List<Propiedad> res = propertiesService.similarProperties(UUID.fromString(propertyId));
			return ResponseEntity.ok(res);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "propertyId inválido"));
		}
	}

	@GetMapping("/recommended")
	public ResponseEntity<?> recommendedProperties(@RequestParam(required = false) String userId) {
		try {
			UUID uid = userId == null ? null : UUID.fromString(userId);
			List<Propiedad> res = propertiesService.recommendedProperties(uid);
			return ResponseEntity.ok(res);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
		}
	}

	@PostMapping("/search-suggestions")
	public ResponseEntity<?> searchSuggestions(@RequestBody Map<String, Object> payload) {
		try {
			List<String> suggestions = propertiesService.searchSuggestions(payload);
			return ResponseEntity.ok(suggestions);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}
}

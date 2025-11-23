package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Document;
import com.edu.unimagdalena.nearby.entities.Universidad;
import com.edu.unimagdalena.nearby.services.SystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Endpoints de sistema y utilidades.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

	// 96. GET /api/system/health
	@GetMapping("/health")
	public ResponseEntity<?> health() {
		return ResponseEntity.ok(systemService.health());
	}

	// 97. GET /api/system/config
	@GetMapping("/config")
	public ResponseEntity<?> config() {
		return ResponseEntity.ok(systemService.config());
	}

	// 98. POST /api/upload/documents
	@PostMapping("/upload/documents")
	public ResponseEntity<?> uploadDocuments(@RequestParam("file") MultipartFile file,
	                                         @RequestParam(value = "userId", required = false) String userId,
	                                         @RequestParam(value = "type", required = false) String type) {
		Document doc = systemService.uploadDocument(file, userId, type);
		return ResponseEntity.accepted().body(Map.of("uploaded", true, "document", doc));
	}

	// 99. GET /api/universities
	@GetMapping("/universities")
	public ResponseEntity<?> listUniversities() {
		List<Universidad> list = systemService.listUniversities();
		return ResponseEntity.ok(list);
	}

	// 100. GET /api/locations/suggest
	@GetMapping("/locations/suggest")
	public ResponseEntity<?> suggestLocations(@RequestParam String q) {
		List<String> suggestions = systemService.suggestLocations(q);
		return ResponseEntity.ok(suggestions);
	}
}

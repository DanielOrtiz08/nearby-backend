package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Reporte;
import com.edu.unimagdalena.nearby.entities.Reply;
import com.edu.unimagdalena.nearby.entities.Review;
import com.edu.unimagdalena.nearby.services.ReviewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints para reseñas y valoraciones.
 */
@RestController
@RequestMapping("/api/reviews")
public class ReviewsController {

	private final ReviewsService reviewsService;

	public ReviewsController(ReviewsService reviewsService) {
		this.reviewsService = reviewsService;
	}

	@PostMapping
	public ResponseEntity<?> createReview(@RequestBody Map<String, Object> dto) {
		try {
			Review r = reviewsService.createReview(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(r);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/property/{propertyId}")
	public ResponseEntity<?> reviewsByProperty(@PathVariable String propertyId) {
		try {
			List<Review> list = reviewsService.reviewsByProperty(propertyId);
			return ResponseEntity.ok(list);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/user/{userId}")
	public ResponseEntity<?> reviewsByUser(@PathVariable String userId) {
		try {
			List<Review> list = reviewsService.reviewsByUser(userId);
			return ResponseEntity.ok(list);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateReview(@PathVariable String id, @RequestBody Map<String, Object> payload) {
		try {
			Review updated = reviewsService.updateReview(id, payload);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteReview(@PathVariable String id) {
		try {
			reviewsService.deleteReview(id);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/{id}/replies")
	public ResponseEntity<?> replyToReview(@PathVariable String id, @RequestBody Map<String, Object> dto) {
		try {
			Reply reply = reviewsService.replyToReview(id, dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(reply);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/{id}/report")
	public ResponseEntity<?> reportReview(@PathVariable String id, @RequestBody Map<String, Object> dto) {
		try {
			Reporte rep = reviewsService.reportReview(id, dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(rep);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}
}

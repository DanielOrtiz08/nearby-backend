package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Payment;
import com.edu.unimagdalena.nearby.services.PaymentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para pagos.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

	private final PaymentsService paymentsService;

	public PaymentsController(PaymentsService paymentsService) {
		this.paymentsService = paymentsService;
	}

	@PostMapping("/intent")
	public ResponseEntity<?> createPaymentIntent(@RequestBody Map<String, Object> dto) {
		try {
			Map<String, Object> res = paymentsService.createPaymentIntent(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(res);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@PostMapping("/confirm")
	public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> dto) {
		try {
			Payment p = paymentsService.confirmPayment(dto);
			return ResponseEntity.ok(p);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	@GetMapping("/history")
	public ResponseEntity<?> paymentHistory(@RequestParam String userId) {
		try {
			List<Payment> list = paymentsService.paymentHistory(UUID.fromString(userId));
			return ResponseEntity.ok(list);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "userId inválido"));
		}
	}

	@PostMapping("/refund")
	public ResponseEntity<?> requestRefund(@RequestBody Map<String, Object> dto) {
		try {
			Map<String, Object> res = paymentsService.requestRefund(dto);
			return ResponseEntity.accepted().body(res);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}
}

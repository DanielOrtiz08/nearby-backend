package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de autenticación y gestión de sesión.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// 1. POST /api/auth/student/register
	@PostMapping("/student/register")
	public ResponseEntity<?> registerStudent(@RequestBody Map<String, Object> payload) {
		try {
			CuentaUsuario created = authService.registerStudent(payload);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 2. POST /api/auth/owner/register
	@PostMapping("/owner/register")
	public ResponseEntity<?> registerOwner(@RequestBody Map<String, Object> payload) {
		try {
			CuentaUsuario created = authService.registerOwner(payload);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}

	// 3. POST /api/auth/login
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, Object> credentials) {
		try {
			Map<String, Object> result = authService.login(credentials);
			return ResponseEntity.ok(result);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		}
	}

	// 4. POST /api/auth/logout
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
									@RequestBody(required = false) Map<String, Object> payload) {
		// acepta token por header "Authorization: Bearer <token>" o por body { "token": "..." }
		authService.logout(authHeader, payload);
		return ResponseEntity.ok(Map.of("message", "logout exitoso"));
	}

	// 5. POST /api/auth/verify-email
	@PostMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@RequestBody Map<String, Object> payload) {
		boolean ok = authService.verifyEmail(payload);
		return ResponseEntity.ok(Map.of("verified", ok));
	}

	// 6. POST /api/auth/resend-otp
	@PostMapping("/resend-otp")
	public ResponseEntity<?> resendOtp(@RequestBody Map<String, Object> payload) {
		boolean sent = authService.resendOtp(payload);
		return ResponseEntity.ok(Map.of("resent", sent));
	}

	// 7. POST /api/auth/forgot-password
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, Object> payload) {
		boolean started = authService.forgotPassword(payload);
		return ResponseEntity.ok(Map.of("started", started));
	}

	// 8. POST /api/auth/reset-password
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, Object> payload) {
		boolean ok = authService.resetPassword(payload);
		if (ok) return ResponseEntity.ok(Map.of("reset", true));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("reset", false));
	}

	// 9. GET /api/auth/me
	@GetMapping("/me")
	public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		try {
			CuentaUsuario u = authService.me(authHeader);
			return ResponseEntity.ok(u);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
		}
	}

	// 10. PUT /api/auth/refresh-token
	@PutMapping("/refresh-token")
	public ResponseEntity<?> refreshToken(@RequestBody Map<String, Object> payload) {
		try {
			Map<String, Object> tokens = authService.refreshToken(payload);
			return ResponseEntity.ok(tokens);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
		}
	}
}

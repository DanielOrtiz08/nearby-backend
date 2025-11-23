package com.edu.unimagdalena.nearby.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Endpoints para perfiles de usuario (students y owners), avatar y notificaciones.
 *
 * Análisis rápido:
 * - Ya existen endpoints CRUD básicos para students/owners, avatar y prefs.
 * - Faltaban endpoints comunes: listado/búsqueda de usuarios, avatar público (GET),
 *   cambio de contraseña, subida/gestión de documentos de identidad, verificación de identidad,
 *   2FA enable/verify/disable, enlace/desenlace de cuentas sociales y endpoints para consultar/asignar Administrador.
 * - Añadimos stubs para estas operaciones para que la API cubra la mayoría de casos de uso.
 * - Recomendación: crear/llenar controladores faltantes (AuthController, PropertiesController, BookingsController, AdminController, etc.) si no existen.
 */
@RestController
@RequestMapping("/api/users")
public class UsersController {

	// 11. GET /api/users/students/{id}
	@GetMapping("/students/{id}")
	public ResponseEntity<?> getStudentProfile(@PathVariable String id) {
		// Obtener perfil estudiante por id
		return ResponseEntity.ok().build();
	}

	// 12. PUT /api/users/students/{id}
	@PutMapping("/students/{id}")
	public ResponseEntity<?> updateStudentProfile(@PathVariable String id, @RequestBody Object payload) {
		// Actualizar perfil estudiante
		return ResponseEntity.ok().build();
	}

	// 13. GET /api/users/owners/{id}
	@GetMapping("/owners/{id}")
	public ResponseEntity<?> getOwnerProfile(@PathVariable String id) {
		// Obtener perfil propietario por id
		return ResponseEntity.ok().build();
	}

	// 14. PUT /api/users/owners/{id}
	@PutMapping("/owners/{id}")
	public ResponseEntity<?> updateOwnerProfile(@PathVariable String id, @RequestBody Object payload) {
		// Actualizar perfil propietario
		return ResponseEntity.ok().build();
	}

	// 15. POST /api/users/upload-avatar
	@PostMapping("/upload-avatar")
	public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
		// Subir foto de perfil
		return ResponseEntity.ok().build();
	}

	// 16. DELETE /api/users/avatar
	@DeleteMapping("/avatar")
	public ResponseEntity<?> deleteAvatar() {
		// Eliminar foto de perfil del usuario autenticado
		return ResponseEntity.noContent().build();
	}

	// 17. PUT /api/users/notification-preferences
	@PutMapping("/notification-preferences")
	public ResponseEntity<?> updateNotificationPreferences(@RequestBody Object prefs) {
		// Actualizar preferencias de notificaciones
		return ResponseEntity.ok().build();
	}

	// ----------------- Nuevos stubs añadidos -----------------

	// Listado general de usuarios (con filtros básicos)
	// GET /api/users
	@GetMapping
	public ResponseEntity<?> listUsers(@RequestParam(required = false) String role,
	                                   @RequestParam(required = false) String q,
	                                   @RequestParam(required = false) Integer page) {
		// Listar/filtrar usuarios (para admin o búsqueda interna)
		return ResponseEntity.ok().build();
	}

	// Búsqueda avanzada de usuarios
	// GET /api/users/search
	@GetMapping("/search")
	public ResponseEntity<?> searchUsers(@RequestParam String q,
	                                     @RequestParam(required = false) String filter) {
		// Buscar usuarios por nombre/email/otros campos
		return ResponseEntity.ok().build();
	}

	// Obtener avatar público de un usuario
	// GET /api/users/avatar/{userId}
	@GetMapping("/avatar/{userId}")
	public ResponseEntity<?> getUserAvatar(@PathVariable String userId) {
		// Retornar la imagen de avatar pública (o 404 si no existe)
		return ResponseEntity.ok().build();
	}

	// Cambio de contraseña por el usuario autenticado / admin
	// PUT /api/users/{id}/change-password
	@PutMapping("/{id}/change-password")
	public ResponseEntity<?> changePassword(@PathVariable String id, @RequestBody Object payload) {
		// Cambiar contraseña (validar actuales/roles/permiso)
		return ResponseEntity.ok().build();
	}

	// Subida de documentos de identidad (identificación, contrato, etc.)
	// POST /api/users/upload-document
	@PostMapping("/upload-document")
	public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
	                                        @RequestParam(required = false) String documentType) {
		// Guardar documento para verificación
		return ResponseEntity.accepted().build();
	}

	// Obtener estado de verificación de identidad de un usuario
	// GET /api/users/verify-identity/status/{userId}
	@GetMapping("/verify-identity/status/{userId}")
	public ResponseEntity<?> identityVerificationStatus(@PathVariable String userId) {
		// Retornar estado (pending, verified, rejected)
		return ResponseEntity.ok().build();
	}

	// 2FA: habilitar
	// POST /api/users/2fa/enable
	@PostMapping("/2fa/enable")
	public ResponseEntity<?> enable2fa(@RequestBody Object payload) {
		// Habilitar 2FA (generar secret / enviar QR)
		return ResponseEntity.ok().build();
	}

	// 2FA: verificar código
	// POST /api/users/2fa/verify
	@PostMapping("/2fa/verify")
	public ResponseEntity<?> verify2fa(@RequestBody Object payload) {
		// Verificar código 2FA suministrado por el usuario
		return ResponseEntity.ok().build();
	}

	// 2FA: deshabilitar
	// POST /api/users/2fa/disable
	@PostMapping("/2fa/disable")
	public ResponseEntity<?> disable2fa(@RequestBody Object payload) {
		// Deshabilitar 2FA (validar contraseña / token)
		return ResponseEntity.ok().build();
	}

	// Enlace de cuenta social (Google, Facebook, etc.)
	// POST /api/users/social/{provider}/link
	@PostMapping("/social/{provider}/link")
	public ResponseEntity<?> linkSocial(@PathVariable String provider, @RequestBody Object payload) {
		// Enlazar cuenta social al usuario
		return ResponseEntity.ok().build();
	}

	// Desenlace de cuenta social
	// DELETE /api/users/social/{provider}/unlink
	@DeleteMapping("/social/{provider}/unlink")
	public ResponseEntity<?> unlinkSocial(@PathVariable String provider) {
		// Desenlazar cuenta social del usuario
		return ResponseEntity.noContent().build();
	}

	// Endpoints mínimos relacionados con la entidad Administrador (ver /entities/Administrador.java)
	// GET /api/users/administrators/{id}
	@GetMapping("/administrators/{id}")
	public ResponseEntity<?> getAdministrator(@PathVariable String id) {
		// Obtener info de Administrador por id (alcançe, estado, fechaAsignacion, usuario asociado)
		return ResponseEntity.ok().build();
	}

	// POST /api/users/administrators/{userId}/assign
	@PostMapping("/administrators/{userId}/assign")
	public ResponseEntity<?> assignAdministrator(@PathVariable String userId, @RequestBody Object payload) {
		// Asignar rol/registro de administrador a cuenta de usuario (solo admin puede)
		return ResponseEntity.ok().build();
	}

	// ----------------- fin nuevos stubs -----------------
}

package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Reservacion;
import com.edu.unimagdalena.nearby.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para bookings / reservas.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingsController {

    private final BookingService bookingService;

    public BookingsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // 33. POST /api/bookings
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> payload) {
        try {
            Reservacion created = bookingService.createBooking(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 34. GET /api/bookings/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(@PathVariable String id) {
        try {
            Reservacion r = bookingService.getBooking(UUID.fromString(id));
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // 35. PUT /api/bookings/{id}/cancel
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id) {
        try {
            Reservacion r = bookingService.cancelBooking(UUID.fromString(id));
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 36. GET /api/bookings/student/{studentId}
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> bookingsByStudent(@PathVariable String studentId) {
        try {
            List<Reservacion> list = bookingService.bookingsByStudent(UUID.fromString(studentId));
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        }
    }

    // 37. GET /api/bookings/owner/{ownerId}
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> bookingsByOwner(@PathVariable String ownerId) {
        try {
            List<Reservacion> list = bookingService.bookingsByOwner(UUID.fromString(ownerId));
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        }
    }

    // 38. PUT /api/bookings/{id}/accept
    @PutMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable String id) {
        try {
            Reservacion r = bookingService.acceptBooking(UUID.fromString(id));
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 39. PUT /api/bookings/{id}/reject
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable String id, @RequestBody(required = false) Map<String, Object> payload) {
        try {
            String reason = payload != null && payload.get("reason") != null ? payload.get("reason").toString() : null;
            Reservacion r = bookingService.rejectBooking(UUID.fromString(id), reason);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 40. POST /api/bookings/{id}/confirm-arrival
    @PostMapping("/{id}/confirm-arrival")
    public ResponseEntity<?> confirmArrival(@PathVariable String id) {
        try {
            Reservacion r = bookingService.confirmArrival(UUID.fromString(id));
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // 41. POST /api/bookings/{id}/complete
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable String id) {
        try {
            Reservacion r = bookingService.completeBooking(UUID.fromString(id));
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "id inválido"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

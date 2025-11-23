package com.edu.unimagdalena.nearby.controllers;

import com.edu.unimagdalena.nearby.entities.Reporte;
import com.edu.unimagdalena.nearby.services.ReportsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Endpoints para reportes y moderación.
 */
@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> reportUser(@RequestBody Map<String, Object> dto) {
        try {
            Reporte r = reportsService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(r);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/properties")
    public ResponseEntity<?> reportProperty(@RequestBody Map<String, Object> dto) {
        try {
            Reporte r = reportsService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(r);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> reportReview(@RequestBody Map<String, Object> dto) {
        try {
            Reporte r = reportsService.createReport(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(r);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-reports")
    public ResponseEntity<?> myReports(@RequestParam String userId) {
        try {
            List<Reporte> list = reportsService.myReports(UUID.fromString(userId));
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId inválido"));
        }
    }

    @PostMapping("/moderation/content-check")
    public ResponseEntity<?> contentCheck(@RequestBody Map<String, Object> dto) {
        try {
            Reporte res = reportsService.contentCheck(dto);
            return ResponseEntity.accepted().body(res);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}

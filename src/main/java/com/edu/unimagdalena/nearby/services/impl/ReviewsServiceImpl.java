package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.*;
import com.edu.unimagdalena.nearby.repositories.*;
import com.edu.unimagdalena.nearby.services.ReviewsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ReviewsServiceImpl implements ReviewsService {

    private final ReviewRepository reviewRepo;
    private final ReplyRepository replyRepo;
    private final CuentaUsuarioRepository cuentaRepo;
    private final PropiedadRepository propiedadRepo;
    private final ReporteRepository reporteRepo;

    public ReviewsServiceImpl(ReviewRepository reviewRepo,
                              ReplyRepository replyRepo,
                              CuentaUsuarioRepository cuentaRepo,
                              PropiedadRepository propiedadRepo,
                              ReporteRepository reporteRepo) {
        this.reviewRepo = reviewRepo;
        this.replyRepo = replyRepo;
        this.cuentaRepo = cuentaRepo;
        this.propiedadRepo = propiedadRepo;
        this.reporteRepo = reporteRepo;
    }

    @Override
    public Review createReview(Map<String, Object> payload) {
        if (payload == null || payload.get("propertyId") == null || payload.get("authorId") == null || payload.get("calificacion") == null) {
            throw new RuntimeException("propertyId, authorId y calificacion son obligatorios");
        }
        UUID propId = UUID.fromString(payload.get("propertyId").toString());
        UUID authorId = UUID.fromString(payload.get("authorId").toString());
        Propiedad prop = propiedadRepo.findById(propId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        CuentaUsuario autor = cuentaRepo.findById(authorId).orElseThrow(() -> new RuntimeException("Autor no encontrado"));

        Review r = new Review();
        r.setPropiedad(prop);
        r.setAutor(autor);
        r.setTitulo(payload.getOrDefault("titulo","").toString());
        r.setContenido(payload.getOrDefault("contenido","").toString());
        try {
            r.setCalificacion(Integer.parseInt(payload.get("calificacion").toString()));
        } catch (Exception ex) {
            throw new RuntimeException("calificacion inválida");
        }
        r.setFechaCreacion(LocalDateTime.now());
        return reviewRepo.save(r);
    }

    @Override
    public List<Review> reviewsByProperty(String propertyId) {
        UUID pid = UUID.fromString(propertyId);
        return reviewRepo.findByPropiedad_IdOrderByFechaCreacionDesc(pid);
    }

    @Override
    public List<Review> reviewsByUser(String userId) {
        UUID uid = UUID.fromString(userId);
        return reviewRepo.findByAutor_IdOrderByFechaCreacionDesc(uid);
    }

    @Override
    public Review updateReview(String id, Map<String, Object> payload) {
        UUID rid = UUID.fromString(id);
        Review r = reviewRepo.findById(rid).orElseThrow(() -> new RuntimeException("Review no encontrada"));
        if (payload.containsKey("titulo")) r.setTitulo(payload.get("titulo").toString());
        if (payload.containsKey("contenido")) r.setContenido(payload.get("contenido").toString());
        if (payload.containsKey("calificacion")) {
            try { r.setCalificacion(Integer.parseInt(payload.get("calificacion").toString())); } catch (Exception ex) {}
        }
        return reviewRepo.save(r);
    }

    @Override
    public void deleteReview(String id) {
        UUID rid = UUID.fromString(id);
        Review r = reviewRepo.findById(rid).orElseThrow(() -> new RuntimeException("Review no encontrada"));
        reviewRepo.delete(r);
    }

    @Override
    public Reply replyToReview(String reviewId, Map<String, Object> payload) {
        if (payload == null || payload.get("authorId") == null || payload.get("contenido") == null) {
            throw new RuntimeException("authorId y contenido son obligatorios");
        }
        UUID rid = UUID.fromString(reviewId);
        Review review = reviewRepo.findById(rid).orElseThrow(() -> new RuntimeException("Review no encontrada"));
        UUID authorId = UUID.fromString(payload.get("authorId").toString());
        CuentaUsuario autor = cuentaRepo.findById(authorId).orElseThrow(() -> new RuntimeException("Autor no encontrado"));

        Reply rep = new Reply();
        rep.setReview(review);
        rep.setAutor(autor);
        rep.setContenido(payload.get("contenido").toString());
        rep.setFechaCreacion(LocalDateTime.now());
        Reply saved = replyRepo.save(rep);
        // attach
        if (review.getReplies() == null) review.setReplies(new ArrayList<>());
        review.getReplies().add(saved);
        reviewRepo.save(review);
        return saved;
    }

    @Override
    public Reporte reportReview(String reviewId, Map<String, Object> payload) {
        UUID rid = UUID.fromString(reviewId);
        Review review = reviewRepo.findById(rid).orElseThrow(() -> new RuntimeException("Review no encontrada"));
        if (payload == null || payload.get("usuarioReportadorId") == null) {
            throw new RuntimeException("usuarioReportadorId es obligatorio");
        }
        UUID reporterId = UUID.fromString(payload.get("usuarioReportadorId").toString());
        CuentaUsuario reporter = cuentaRepo.findById(reporterId).orElseThrow(() -> new RuntimeException("Usuario reportador no encontrado"));
        Reporte rep = new Reporte();
        // TipoReporte not set (optional) — set descripcion con contexto
        rep.setUsuarioReportador(reporter);
        rep.setFechaReporte(LocalDateTime.now());
        rep.setDescripcion("Reporte de review id=" + review.getId() + ". " + payload.getOrDefault("descripcion",""));
        reporteRepo.save(rep);
        return rep;
    }
}

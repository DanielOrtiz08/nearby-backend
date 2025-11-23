package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.*;
import com.edu.unimagdalena.nearby.repositories.*;
import com.edu.unimagdalena.nearby.services.SearchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    private final PropiedadRepository propiedadRepo;
    private final SearchHistoryRepository historyRepo;
    private final SearchAlertRepository alertRepo;
    private final CuentaUsuarioRepository cuentaRepo;

    public SearchServiceImpl(PropiedadRepository propiedadRepo,
                             SearchHistoryRepository historyRepo,
                             SearchAlertRepository alertRepo,
                             CuentaUsuarioRepository cuentaRepo) {
        this.propiedadRepo = propiedadRepo;
        this.historyRepo = historyRepo;
        this.alertRepo = alertRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public List<String> suggestions(String q) {
        if (q == null || q.isBlank()) return Collections.emptyList();
        List<com.edu.unimagdalena.nearby.entities.Propiedad> props = propiedadRepo.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(q, q);
        return props.stream().map(Propiedad::getNombre).distinct().limit(10).collect(Collectors.toList());
    }

    @Override
    public void saveHistory(Map<String, Object> payload) {
        if (payload == null || payload.get("query") == null) throw new RuntimeException("query es obligatorio");
        SearchHistory h = new SearchHistory();
        if (payload.get("userId") != null) {
            UUID uid = UUID.fromString(payload.get("userId").toString());
            cuentaRepo.findById(uid).ifPresent(u -> h.setUsuario(u));
        }
        h.setQuery(payload.get("query").toString());
        h.setCreatedAt(LocalDateTime.now());
        historyRepo.save(h);
    }

    @Override
    public List<?> getHistory(UUID userId) {
        if (userId == null) return Collections.emptyList();
        return historyRepo.findByUsuario_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void createAlert(Map<String, Object> payload) {
        if (payload == null || payload.get("userId") == null || payload.get("criteria") == null) throw new RuntimeException("userId y criteria son obligatorios");
        UUID uid = UUID.fromString(payload.get("userId").toString());
        com.edu.unimagdalena.nearby.entities.CuentaUsuario u = cuentaRepo.findById(uid).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        SearchAlert a = new SearchAlert();
        a.setUsuario(u);
        a.setCriteria(payload.get("criteria").toString());
        a.setCreatedAt(LocalDateTime.now());
        alertRepo.save(a);
    }

    @Override
    public List<?> listAlerts(UUID userId) {
        if (userId == null) return Collections.emptyList();
        return alertRepo.findByUsuario_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public void deleteAlert(UUID alertId, UUID userId) {
        SearchAlert a = alertRepo.findById(alertId).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
        if (!a.getUsuario().getId().equals(userId)) throw new RuntimeException("No autorizado");
        alertRepo.delete(a);
    }
}

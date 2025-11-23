package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Mensaje;
import com.edu.unimagdalena.nearby.entities.Reporte;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.enums.TipoReporte;
import com.edu.unimagdalena.nearby.repositories.MensajeRepository;
import com.edu.unimagdalena.nearby.repositories.ReporteRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.services.ReportsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ReportsServiceImpl implements ReportsService {

    private final ReporteRepository reporteRepo;
    private final CuentaUsuarioRepository cuentaRepo;
    private final MensajeRepository mensajeRepo;

    public ReportsServiceImpl(ReporteRepository reporteRepo,
                              CuentaUsuarioRepository cuentaRepo,
                              MensajeRepository mensajeRepo) {
        this.reporteRepo = reporteRepo;
        this.cuentaRepo = cuentaRepo;
        this.mensajeRepo = mensajeRepo;
    }

    @Override
    public Reporte createReport(Map<String, Object> payload) {
        if (payload == null || payload.get("tipoDeReporte") == null || payload.get("usuarioReportadorId") == null) {
            throw new RuntimeException("tipoDeReporte y usuarioReportadorId son obligatorios");
        }
        Reporte r = new Reporte();
        try {
            r.setTipoDeReporte(TipoReporte.valueOf(payload.get("tipoDeReporte").toString()));
        } catch (IllegalArgumentException ignored) {
            // si no coincide, se puede dejar nulo o lanzar; aquí lanzamos para ser explícitos
            throw new RuntimeException("tipoDeReporte inválido");
        }

        UUID userId = UUID.fromString(payload.get("usuarioReportadorId").toString());
        CuentaUsuario reporter = cuentaRepo.findById(userId).orElseThrow(() -> new RuntimeException("Usuario reportador no encontrado"));
        r.setUsuarioReportador(reporter);
        r.setFechaReporte(LocalDateTime.now());

        if (payload.get("mensajeObjetivoId") != null) {
            UUID msgId = UUID.fromString(payload.get("mensajeObjetivoId").toString());
            Mensaje m = mensajeRepo.findById(msgId).orElseThrow(() -> new RuntimeException("Mensaje objetivo no encontrado"));
            r.setObjetivo(m);
        }
        r.setDescripcion(payload.getOrDefault("descripcion","").toString());
        return reporteRepo.save(r);
    }

    @Override
    public List<Reporte> listReports() {
        return reporteRepo.findAll();
    }

    @Override
    public List<Reporte> myReports(UUID userId) {
        // buscar reportes por usuario reportador
        return reporteRepo.findAll().stream().filter(r -> r.getUsuarioReportador() != null && r.getUsuarioReportador().getId().equals(userId)).toList();
    }

    @Override
    public Reporte getReport(UUID id) {
        return reporteRepo.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    @Override
    public Reporte contentCheck(Map<String, Object> payload) {
        // Simulación de verificación automatizada: recibe { "content": "texto" }
        if (payload == null || payload.get("content") == null) {
            throw new RuntimeException("content es obligatorio");
        }
        String content = payload.get("content").toString().toLowerCase();
        Reporte r = new Reporte();
        r.setFechaReporte(LocalDateTime.now());
        r.setDescripcion("Auto-check: " + (content.contains("prohibido") ? "FLAGGED" : "OK"));
        // no persistimos como reporte real, devolvemos el resultado simulado
        return r;
    }
}

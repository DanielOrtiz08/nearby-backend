package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Reporte;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.repositories.PropiedadRepository;
import com.edu.unimagdalena.nearby.repositories.ReporteRepository;
import com.edu.unimagdalena.nearby.services.AdminService;
import com.edu.unimagdalena.nearby.enums.Rol;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final CuentaUsuarioRepository cuentaRepo;
    private final PropiedadRepository propiedadRepo;
    private final ReporteRepository reporteRepo;

    public AdminServiceImpl(CuentaUsuarioRepository cuentaRepo,
                            PropiedadRepository propiedadRepo,
                            ReporteRepository reporteRepo) {
        this.cuentaRepo = cuentaRepo;
        this.propiedadRepo = propiedadRepo;
        this.reporteRepo = reporteRepo;
    }

    @Override
    public List<CuentaUsuario> listUsers() {
        // Obtener todos los usuarios y sanitizarlos (no exponer contrasena)
        List<CuentaUsuario> all = cuentaRepo.findAll();
        return all.stream().map(this::sanitizeUser).collect(Collectors.toList());
    }

    private CuentaUsuario sanitizeUser(CuentaUsuario u) {
        if (u == null) return null;
        // crear nueva instancia y copiar campos no-sensibles
        CuentaUsuario s = new CuentaUsuario();
        s.setId(u.getId());
        s.setUsuario(u.getUsuario());
        s.setRol(u.getRol());
        s.setActivo(u.isActivo());
        s.setFechaRegistro(u.getFechaRegistro());
        // no copiar contrasenaHasheada
        return s;
    }

    @Override
    public CuentaUsuario changeUserStatus(UUID id, Map<String, Object> payload) {
        CuentaUsuario user = cuentaRepo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (payload != null && payload.containsKey("activo")) {
            Object v = payload.get("activo");
            boolean activo;
            if (v instanceof Boolean) activo = (Boolean) v;
            else activo = Boolean.parseBoolean(String.valueOf(v));
            user.setActivo(activo);
            cuentaRepo.save(user);
        } else if (payload != null && payload.containsKey("rol")) {
            Object r = payload.get("rol");
            if (r != null) {
                try {
                    // usar Rol enum directamente
                    user.setRol(Rol.valueOf(r.toString()));
                    cuentaRepo.save(user);
                } catch (IllegalArgumentException ex) {
                    throw new RuntimeException("Rol inválido");
                }
            }
        } else {
            throw new RuntimeException("Payload inválido: se espera la clave 'activo' o 'rol'");
        }
        return sanitizeUser(user);
    }

    @Override
    public List<Propiedad> pendingProperties() {
        return propiedadRepo.findByEstaVerificadaFalse();
    }

    @Override
    public Propiedad approveProperty(UUID id) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        p.setEstaVerificada(true);
        // clear any previous rejection marker in descripcion if present (opcional)
        propiedadRepo.save(p);
        return p;
    }

    @Override
    public Propiedad rejectProperty(UUID id, String reason) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        p.setEstaVerificada(false);
        if (reason != null && !reason.isBlank()) {
            String prev = p.getDescripcion() != null ? p.getDescripcion() : "";
            String marker = prev + (prev.isEmpty() ? "" : " ") + "[RECHAZADA: " + reason + "]";
            p.setDescripcion(marker);
        }
        propiedadRepo.save(p);
        return p;
    }

    @Override
    public List<Reporte> listReports() {
        return reporteRepo.findAll();
    }

    @Override
    public Reporte resolveReport(UUID id, String resolution) {
        Reporte r = reporteRepo.findById(id).orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        // Simplemente marcar la resolución en la descripción
        String prev = r.getDescripcion() != null ? r.getDescripcion() : "";
        String note = prev + (prev.isEmpty() ? "" : " ") + "[RESUELTO: " + (resolution != null ? resolution : "sin nota") + "]";
        r.setDescripcion(note);
        reporteRepo.save(r);
        return r;
    }

    @Override
    public Map<String, Object> dashboardStats() {
        Map<String, Object> m = new HashMap<>();
        long totalUsers = cuentaRepo.count();
        long activeUsers = cuentaRepo.countByActivoTrue();
        long totalProperties = propiedadRepo.count();
        long pendingProperties = propiedadRepo.countByEstaVerificadaFalse();
        long totalReports = reporteRepo.count();

        m.put("totalUsers", totalUsers);
        m.put("activeUsers", activeUsers);
        m.put("totalProperties", totalProperties);
        m.put("pendingProperties", pendingProperties);
        m.put("totalReports", totalReports);
        // simples porcentajes
        m.put("percentVerifiedProperties", totalProperties == 0 ? 0 : ((totalProperties - pendingProperties) * 100.0 / totalProperties));
        m.put("generatedAt", new Date());
        return m;
    }

    @Override
    public Map<String, Object> analytics() {
        // Retornar métricas básicas y listas pequeñas para el panel administrativo
        Map<String, Object> m = new HashMap<>();
        m.putAll(dashboardStats());
        // ejemplo: top 5 propiedades pendientes (id + nombre)
        List<Propiedad> pending = propiedadRepo.findByEstaVerificadaFalse();
        List<Map<String, Object>> topPending = pending.stream().limit(5).map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", p.getId());
            item.put("nombre", p.getNombre());
            item.put("estaVerificada", p.isEstaVerificada());
            return item;
        }).collect(Collectors.toList());
        m.put("topPendingProperties", topPending);
        return m;
    }
}

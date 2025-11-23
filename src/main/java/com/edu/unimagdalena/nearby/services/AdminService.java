package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Reporte;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    List<CuentaUsuario> listUsers();
    CuentaUsuario changeUserStatus(UUID id, Map<String, Object> payload);
    List<Propiedad> pendingProperties();
    Propiedad approveProperty(UUID id);
    Propiedad rejectProperty(UUID id, String reason);
    List<Reporte> listReports();
    Reporte resolveReport(UUID id, String resolution);
    Map<String, Object> dashboardStats();
    Map<String, Object> analytics();
}

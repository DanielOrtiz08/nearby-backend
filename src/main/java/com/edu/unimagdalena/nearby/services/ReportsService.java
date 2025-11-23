package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Reporte;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ReportsService {
    Reporte createReport(Map<String, Object> payload);
    List<Reporte> listReports();
    List<Reporte> myReports(UUID userId);
    Reporte getReport(UUID id);
    Reporte contentCheck(Map<String, Object> payload);
}

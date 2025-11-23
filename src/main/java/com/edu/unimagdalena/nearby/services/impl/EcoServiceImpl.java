package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.EcoCertification;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.repositories.EcoCertificationRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.repositories.PropiedadRepository;
import com.edu.unimagdalena.nearby.services.EcoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EcoServiceImpl implements EcoService {

    private final EcoCertificationRepository ecoRepo;
    private final CuentaUsuarioRepository cuentaRepo;
    private final PropiedadRepository propiedadRepo;

    public EcoServiceImpl(EcoCertificationRepository ecoRepo,
                          CuentaUsuarioRepository cuentaRepo,
                          PropiedadRepository propiedadRepo) {
        this.ecoRepo = ecoRepo;
        this.cuentaRepo = cuentaRepo;
        this.propiedadRepo = propiedadRepo;
    }

    @Override
    public EcoCertification applyCertification(java.util.Map<String, Object> payload) {
        if (payload == null || payload.get("solicitanteId") == null) {
            throw new RuntimeException("solicitanteId es obligatorio");
        }
        UUID solicitanteId = UUID.fromString(payload.get("solicitanteId").toString());
        CuentaUsuario solicitante = cuentaRepo.findById(solicitanteId).orElseThrow(() -> new RuntimeException("Solicitante no encontrado"));

        EcoCertification e = new EcoCertification();
        e.setSolicitante(solicitante);
        if (payload.get("propiedadId") != null) {
            UUID propId = UUID.fromString(payload.get("propiedadId").toString());
            Propiedad propiedad = propiedadRepo.findById(propId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
            e.setPropiedad(propiedad);
        }
        e.setEstado("PENDING");
        e.setFechaSolicitud(LocalDateTime.now());
        e.setVerificado(false);
        e.setNotas(payload.getOrDefault("notas", "").toString());
        return ecoRepo.save(e);
    }

    @Override
    public List<EcoCertification> listCertifications() {
        return ecoRepo.findAll();
    }

    @Override
    public EcoCertification verifyCertification(UUID id) {
        EcoCertification e = ecoRepo.findById(id).orElseThrow(() -> new RuntimeException("Certificación no encontrada"));
        e.setVerificado(true);
        e.setEstado("VERIFIED");
        return ecoRepo.save(e);
    }
}

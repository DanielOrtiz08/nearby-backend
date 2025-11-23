package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.EcoCertification;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EcoService {
    EcoCertification applyCertification(Map<String, Object> payload);
    List<EcoCertification> listCertifications();
    EcoCertification verifyCertification(UUID id);
}

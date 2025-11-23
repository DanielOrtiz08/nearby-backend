package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Document;
import com.edu.unimagdalena.nearby.entities.Universidad;
import com.edu.unimagdalena.nearby.entities.Ubicacion;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.repositories.DocumentRepository;
import com.edu.unimagdalena.nearby.repositories.UniversidadRepository;
import com.edu.unimagdalena.nearby.repositories.UbicacionRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.services.SystemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SystemServiceImpl implements SystemService {

    private final UniversidadRepository universidadRepo;
    private final UbicacionRepository ubicacionRepo;
    private final DocumentRepository documentRepo;
    private final CuentaUsuarioRepository cuentaRepo;
    private final Instant startedAt = Instant.now();

    public SystemServiceImpl(UniversidadRepository universidadRepo,
                             UbicacionRepository ubicacionRepo,
                             DocumentRepository documentRepo,
                             CuentaUsuarioRepository cuentaRepo) {
        this.universidadRepo = universidadRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.documentRepo = documentRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public Map<String, Object> health() {
        Map<String, Object> m = new HashMap<>();
        m.put("status", "UP");
        m.put("startedAt", startedAt.toString());
        m.put("now", Instant.now().toString());
        m.put("uptimeSeconds", Instant.now().getEpochSecond() - startedAt.getEpochSecond());
        return m;
    }

    @Override
    public Map<String, Object> config() {
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("maxUploadSizeBytes", 50 * 1024 * 1024);
        cfg.put("supportedCurrencies", List.of("COP", "USD"));
        cfg.put("version", "0.1.0");
        cfg.put("features", Map.of("payments", true, "chat", true, "ecoCert", true));
        return cfg;
    }

    @Override
    public Document uploadDocument(MultipartFile file, String userId, String type) {
        if (file == null || file.isEmpty()) throw new RuntimeException("file es obligatorio");
        String tmpDir = System.getProperty("java.io.tmpdir");
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        File dest = new File(tmpDir, filename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo: " + e.getMessage());
        }
        Document doc = new Document();
        doc.setFilename(file.getOriginalFilename());
        doc.setPath(dest.getAbsolutePath());
        doc.setUploadedAt(LocalDateTime.now());
        doc.setType(type == null ? "generic" : type);
        if (userId != null) {
            try {
                UUID uid = UUID.fromString(userId);
                CuentaUsuario u = cuentaRepo.findById(uid).orElse(null);
                doc.setUploader(u);
            } catch (Exception ignored) {}
        }
        return documentRepo.save(doc);
    }

    @Override
    public List<Universidad> listUniversities() {
        return universidadRepo.findAll();
    }

    @Override
    public List<String> suggestLocations(String q) {
        if (q == null || q.isBlank()) return Collections.emptyList();
        List<Ubicacion> list = ubicacionRepo.findByValorContainingIgnoreCase(q);
        return list.stream().map(Ubicacion::getValor).distinct().limit(10).collect(Collectors.toList());
    }
}

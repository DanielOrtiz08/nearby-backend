package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Arrendador;
import com.edu.unimagdalena.nearby.entities.Propiedad;
import com.edu.unimagdalena.nearby.entities.Ubicacion;
import com.edu.unimagdalena.nearby.entities.Universidad;
import com.edu.unimagdalena.nearby.repositories.ArrendadorRepository;
import com.edu.unimagdalena.nearby.repositories.PropiedadRepository;
import com.edu.unimagdalena.nearby.repositories.UbicacionRepository;
import com.edu.unimagdalena.nearby.repositories.UniversidadRepository;
import com.edu.unimagdalena.nearby.services.PropertiesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertiesServiceImpl implements PropertiesService {

    private final PropiedadRepository propiedadRepo;
    private final UbicacionRepository ubicacionRepo;
    private final ArrendadorRepository arrendadorRepo;
    private final UniversidadRepository universidadRepo;

    public PropertiesServiceImpl(PropiedadRepository propiedadRepo,
                                 UbicacionRepository ubicacionRepo,
                                 ArrendadorRepository arrendadorRepo,
                                 UniversidadRepository universidadRepo) {
        this.propiedadRepo = propiedadRepo;
        this.ubicacionRepo = ubicacionRepo;
        this.arrendadorRepo = arrendadorRepo;
        this.universidadRepo = universidadRepo;
    }

    @Override
    public Propiedad createProperty(Map<String, Object> payload) {
        if (payload == null || payload.get("nombre") == null || payload.get("ownerId") == null) {
            throw new RuntimeException("nombre y ownerId son obligatorios");
        }
        Propiedad p = new Propiedad();
        p.setNombre(payload.get("nombre").toString());
        p.setDescripcion(payload.getOrDefault("descripcion", "").toString());
        p.setTipoDePropiedad(payload.getOrDefault("tipoDePropiedad", "").toString());
        p.setEstaVerificada(Boolean.parseBoolean(payload.getOrDefault("estaVerificada", "false").toString()));
        p.setEsAmbientalista(Boolean.parseBoolean(payload.getOrDefault("esAmbientalista", "false").toString()));

        // dueño
        UUID ownerId = UUID.fromString(payload.get("ownerId").toString());
        Arrendador owner = arrendadorRepo.findById(ownerId).orElseThrow(() -> new RuntimeException("Arrendador no encontrado"));
        p.setDueño(owner);

        // ubicacion
        if (payload.get("ubicacionId") != null) {
            UUID ubId = UUID.fromString(payload.get("ubicacionId").toString());
            Ubicacion u = ubicacionRepo.findById(ubId).orElseThrow(() -> new RuntimeException("Ubicación no encontrada"));
            p.setUbicacion(u);
        }

        // imagenes opcionales
        p.setImagenesUrls(new ArrayList<>());

        return propiedadRepo.save(p);
    }

    @Override
    public List<Propiedad> listProperties(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return propiedadRepo.findAll();
        }
        // simple filtering: q by name/description
        if (filters.containsKey("q")) {
            String q = filters.get("q").toString();
            return propiedadRepo.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(q, q);
        }
        return propiedadRepo.findAll();
    }

    @Override
    public Propiedad getProperty(UUID id) {
        return propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
    }

    @Override
    public Propiedad updateProperty(UUID id, Map<String, Object> payload) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (payload.containsKey("nombre")) p.setNombre(payload.get("nombre").toString());
        if (payload.containsKey("descripcion")) p.setDescripcion(payload.get("descripcion").toString());
        if (payload.containsKey("tipoDePropiedad")) p.setTipoDePropiedad(payload.get("tipoDePropiedad").toString());
        if (payload.containsKey("estaVerificada")) p.setEstaVerificada(Boolean.parseBoolean(payload.get("estaVerificada").toString()));
        if (payload.containsKey("esAmbientalista")) p.setEsAmbientalista(Boolean.parseBoolean(payload.get("esAmbientalista").toString()));
        return propiedadRepo.save(p);
    }

    @Override
    public void deleteProperty(UUID id) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        propiedadRepo.delete(p);
    }

    @Override
    public Propiedad changePropertyStatus(UUID id, String status) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        // usamos estado boolean estaVerificada como ejemplo
        if ("approve".equalsIgnoreCase(status) || "approved".equalsIgnoreCase(status)) {
            p.setEstaVerificada(true);
        } else if ("reject".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
            p.setEstaVerificada(false);
        }
        return propiedadRepo.save(p);
    }

    @Override
    public List<Propiedad> propertiesByOwner(UUID ownerId) {
        return propiedadRepo.findByDueño_Id(ownerId);
    }

    @Override
    public List<String> addPropertyPhotos(UUID id, MultipartFile[] files) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (p.getImagenesUrls() == null) p.setImagenesUrls(new ArrayList<>());
        String tmpDir = System.getProperty("java.io.tmpdir");
        List<String> added = new ArrayList<>();
        for (MultipartFile f : files) {
            String filename = UUID.randomUUID() + "_" + f.getOriginalFilename();
            File dest = new File(tmpDir, filename);
            try {
                f.transferTo(dest);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar archivo: " + e.getMessage());
            }
            String url = "file://" + dest.getAbsolutePath();
            p.getImagenesUrls().add(url);
            added.add(url);
        }
        propiedadRepo.save(p);
        return added;
    }

    @Override
    public void deletePropertyPhoto(UUID id, String photoUrl) {
        Propiedad p = propiedadRepo.findById(id).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (p.getImagenesUrls() == null || !p.getImagenesUrls().remove(photoUrl)) {
            throw new RuntimeException("Foto no encontrada en la propiedad");
        }
        propiedadRepo.save(p);
    }

    @Override
    public List<Propiedad> advancedSearch(Map<String, Object> query) {
        // implementacion simple delegando a listProperties con key 'q'
        return listProperties(query);
    }

    @Override
    public List<Propiedad> propertiesNearUniversity(UUID universityId) {
        Universidad uni = universidadRepo.findById(universityId).orElseThrow(() -> new RuntimeException("Universidad no encontrada"));
        if (uni.getUbicacion() == null) return Collections.emptyList();
        return propiedadRepo.findByUbicacion_Id(uni.getUbicacion().getId());
    }

    @Override
    public List<Propiedad> featuredProperties() {
        // Ejemplo: propiedades verificadas y ambientalistas primero
        List<Propiedad> all = propiedadRepo.findAll();
        return all.stream()
                .filter(Propiedad::isEstaVerificada)
                .sorted(Comparator.comparing(Propiedad::isEsAmbientalista).reversed())
                .limit(20)
                .collect(Collectors.toList());
    }

    @Override
    public List<Propiedad> similarProperties(UUID propertyId) {
        Propiedad p = propiedadRepo.findById(propertyId).orElseThrow(() -> new RuntimeException("Propiedad no encontrada"));
        if (p.getTipoDePropiedad() == null) return Collections.emptyList();
        return propiedadRepo.findByTipoDePropiedadAndIdNot(p.getTipoDePropiedad(), propertyId).stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public List<Propiedad> recommendedProperties(UUID userId) {
        // Recomendación simple: propiedades destacadas aleatorias
        List<Propiedad> featured = featuredProperties();
        Collections.shuffle(featured);
        return featured.stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public List<String> searchSuggestions(Map<String, Object> payload) {
        String q = payload != null && payload.get("q") != null ? payload.get("q").toString() : "";
        if (q.isBlank()) return Collections.emptyList();
        List<Propiedad> matches = propiedadRepo.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(q, q);
        return matches.stream().map(Propiedad::getNombre).distinct().limit(10).collect(Collectors.toList());
    }
}

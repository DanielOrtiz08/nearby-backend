package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Propiedad;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PropertiesService {
    Propiedad createProperty(Map<String, Object> payload);
    List<Propiedad> listProperties(Map<String, Object> filters);
    Propiedad getProperty(UUID id);
    Propiedad updateProperty(UUID id, Map<String, Object> payload);
    void deleteProperty(UUID id);
    Propiedad changePropertyStatus(UUID id, String status);
    List<Propiedad> propertiesByOwner(UUID ownerId);
    List<String> addPropertyPhotos(UUID id, MultipartFile[] files);
    void deletePropertyPhoto(UUID id, String photoUrl);
    List<Propiedad> advancedSearch(Map<String, Object> query);
    List<Propiedad> propertiesNearUniversity(UUID universityId);
    List<Propiedad> featuredProperties();
    List<Propiedad> similarProperties(UUID propertyId);
    List<Propiedad> recommendedProperties(UUID userId);
    List<String> searchSuggestions(Map<String, Object> payload);
}

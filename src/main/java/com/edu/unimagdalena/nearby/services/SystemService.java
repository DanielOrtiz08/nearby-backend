package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Document;
import com.edu.unimagdalena.nearby.entities.Universidad;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SystemService {
    Map<String, Object> health();
    Map<String, Object> config();
    Document uploadDocument(MultipartFile file, String userId, String type);
    List<Universidad> listUniversities();
    List<String> suggestLocations(String q);
}

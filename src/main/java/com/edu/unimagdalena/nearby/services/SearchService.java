package com.edu.unimagdalena.nearby.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SearchService {
    List<String> suggestions(String q);
    void saveHistory(Map<String, Object> payload);
    List<?> getHistory(UUID userId);
    void createAlert(Map<String, Object> payload);
    List<?> listAlerts(UUID userId);
    void deleteAlert(UUID alertId, UUID userId);
}

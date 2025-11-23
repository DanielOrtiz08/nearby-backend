package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, UUID> {
    List<SearchHistory> findByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId);
}

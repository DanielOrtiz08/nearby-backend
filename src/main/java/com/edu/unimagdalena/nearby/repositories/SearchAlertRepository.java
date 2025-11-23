package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.SearchAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchAlertRepository extends JpaRepository<SearchAlert, UUID> {
    List<SearchAlert> findByUsuario_IdOrderByCreatedAtDesc(UUID usuarioId);
}

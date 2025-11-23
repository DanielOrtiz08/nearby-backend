package com.edu.unimagdalena.nearby.repositories;

import com.edu.unimagdalena.nearby.entities.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    List<Favorito> findByEstudiante_Id(UUID estudianteId);
    boolean existsByEstudiante_IdAndPropiedad_Id(UUID estudianteId, UUID propiedadId);
    void deleteByEstudiante_IdAndPropiedad_Id(UUID estudianteId, UUID propiedadId);
}
